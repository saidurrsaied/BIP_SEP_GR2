package com.parking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.usermanagement.User;
import com.parking.usermanagement.UserRole;
import com.parking.usermanagement.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for REST API endpoints.
 * Tests the complete flow from user registration through parking operations.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("REST API Integration Tests")
class RestIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private String adminToken;
    private String citizenToken;

    @BeforeEach
    void setup() throws Exception {
        // Register a citizen user
        mvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "citizen@test.com",
                        "password", "TestPassword123",
                        "numberplate", "AB123CD",
                        "role", "CITIZEN"
                ))))
                .andExpect(status().isOk());

        // Register an admin user
        mvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "admin@test.com",
                        "password", "AdminPass123",
                        "numberplate", "XY789ZW",
                        "role", "ADMIN"
                ))))
                .andExpect(status().isOk());

        // Login as citizen
        MvcResult citizenLoginResult = mvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "citizen@test.com",
                        "password", "TestPassword123"
                ))))
                .andExpect(status().isOk())
                .andReturn();
        citizenToken = objectMapper.readTree(citizenLoginResult.getResponse().getContentAsString())
                .get("token").asText();

        // Login as admin
        MvcResult adminLoginResult = mvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "admin@test.com",
                        "password", "AdminPass123"
                ))))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = objectMapper.readTree(adminLoginResult.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testUserRegistration() throws Exception {
        mvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "newuser@test.com",
                        "password", "NewUserPass123",
                        "numberplate", "CD456EF",
                        "role", "CITIZEN"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("newuser@test.com")))
                .andExpect(jsonPath("$.role", is("CITIZEN")));
    }

    @Test
    @DisplayName("Should reject duplicate email registration")
    void testDuplicateEmailRegistration() throws Exception {
        mvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "citizen@test.com",
                        "password", "TestPassword123",
                        "numberplate", "GH789IJ",
                        "role", "CITIZEN"
                ))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should login successfully and return token")
    void testLogin() throws Exception {
        mvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "citizen@test.com",
                        "password", "TestPassword123"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email", is("citizen@test.com")));
    }

    @Test
    @DisplayName("Should reject invalid credentials")
    void testInvalidLogin() throws Exception {
        mvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "citizen@test.com",
                        "password", "WrongPassword"
                ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get current user info with valid token")
    void testGetCurrentUser() throws Exception {
        mvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + citizenToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("citizen@test.com")));
    }

    @Test
    @DisplayName("Should reject request without token")
    void testGetCurrentUserWithoutToken() throws Exception {
        mvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should list zones without authentication")
    void testListZonesPublic() throws Exception {
        mvc.perform(get("/api/zones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("Should allow admin to create zone")
    void testAdminCreateZone() throws Exception {
        mvc.perform(post("/api/zones")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "name", "Test Zone",
                        "address", "123 Test Street",
                        "city", "Test City",
                        "latitude", 51.5,
                        "longitude", 7.5,
                        "pricingPolicy", Map.of(
                                "hourlyRateCents", 500,
                                "slowMultiplier", 15,
                                "fastMultiplier", 20
                        )
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Zone")))
                .andExpect(jsonPath("$.city", is("Test City")));
    }

    @Test
    @DisplayName("Should reject citizen trying to create zone")
    void testCitizenCannotCreateZone() throws Exception {
        mvc.perform(post("/api/zones")
                .header("Authorization", "Bearer " + citizenToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "name", "Test Zone",
                        "address", "123 Test Street",
                        "city", "Test City",
                        "latitude", 51.5,
                        "longitude", 7.5
                ))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should register new citizen")
    void testCitizenRegistration() throws Exception {
        mvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "user@example.com",
                        "password", "SecurePass123",
                        "numberplate", "LM123NO",
                        "role", "CITIZEN"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("CITIZEN")));
    }

    @Test
    @DisplayName("Should enforce password minimum length")
    void testPasswordTooShort() throws Exception {
        mvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                        "email", "short@test.com",
                        "password", "Short7",
                        "numberplate", "PQ123RS",
                        "role", "CITIZEN"
                ))))
                .andExpect(status().isBadRequest());
    }
}