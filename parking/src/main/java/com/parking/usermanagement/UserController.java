package com.parking.usermanagement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        UserRole role = request.role() != null ? request.role() : UserRole.CITIZEN;
        return ResponseEntity.ok(userService.registerUser(request.email(), request.password(), request.numberplate(), role));
    }

    @PostMapping("/login")
    public ResponseEntity<UserService.AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        // 1. Check of wachtwoord en email kloppen
        UserService.AuthResponse response = userService.authenticateUser(request.email(), request.password());

        // 2. Maak het token aan voor Spring Security
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                null,
                // Let op: pas dit aan naar de rol die bij jouw project hoort (bijv. ROLE_CITIZEN)
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 3. Maak de sessie aan (genereert de JSESSIONID cookie)
        HttpSession session = httpRequest.getSession(true);

        // 4. DE FIX: Sla de inlog-context op in de sessie-doos!
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        // 5. Stuur response terug naar SvelteKit
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<UserRole> getUserRole(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserRole(id));
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // Geef ook errors netjes als JSON terug
            return ResponseEntity.status(401).body(java.util.Map.of("error", "Niet ingelogd"));
        }

        // Verpak het e-mailadres in een netjes JSON object!
        return ResponseEntity.ok(java.util.Map.of("email", authentication.getPrincipal()));
    }

    public record RegisterRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, message = "Password must be at least 8 characters")
            String password,

            @NotBlank(message = "Numberplate is required")
            String numberplate,

            UserRole role
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}
}
