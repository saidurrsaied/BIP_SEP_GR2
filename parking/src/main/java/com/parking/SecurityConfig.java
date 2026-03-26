package com.parking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Voor nu even gehardcodeerd voor het gemak
    private final String frontendUrl = "http://localhost:5173";

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable()) // Keep disabled for easy testing, or see note below
                .authorizeHttpRequests(auth -> auth
                // 1. Permitted Swagger/OpenAPI paths
                .requestMatchers(
                        "/v3/api-docs/**",      // The raw JSON/YAML spec
                        "/v3/api-docs.yaml",    // Specific YAML endpoint
                        "/swagger-ui/**",       // Swagger UI static resources
                        "/swagger-ui.html"      // The main entry point redirect
                ).permitAll()

                // 2. Your existing public endpoints
                .requestMatchers("/api/users/login", "/api/users/register", "/api/zones/**").permitAll()

                // 3. Secure everything else
                .anyRequest().authenticated()
                )
                // ... (Rest of your session and logout config)
                .sessionManagement(session -> session.maximumSessions(1));

        return http.build();
        }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Verplicht voor SessionID/Cookies
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN", "Accept"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
