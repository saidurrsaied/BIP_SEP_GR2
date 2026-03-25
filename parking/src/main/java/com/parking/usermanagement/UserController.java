package com.parking.usermanagement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        UserRole role = request.role() != null ? request.role() : UserRole.CITIZEN;
        return ResponseEntity.ok(userService.registerUser(request.email(), request.password(), role));
    }

    @PostMapping("/login")
    public ResponseEntity<UserService.AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticateUser(request.email(), request.password()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<UserRole> getUserRole(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserRole(id));
    }

    public record RegisterRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password,

            UserRole role
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}
}
