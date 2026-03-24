package com.parking.usermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        UserRole role = request.role() != null ? request.role() : UserRole.CITIZEN;
        return ResponseEntity.ok(userService.registerUser(request.email(), request.password(), role));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
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

    public record RegisterRequest(String email, String password, UserRole role) {}
    public record LoginRequest(String email, String password) {}
}
