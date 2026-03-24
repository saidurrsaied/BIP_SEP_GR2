package com.parking.usermanagement;

import com.parking.usermanagement.internal.PasswordHasher;
import com.parking.usermanagement.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public User registerUser(String email, String password, UserRole role) {
        String userId = UUID.randomUUID().toString();
        User user = User.builder()
                .userId(userId)
                .email(email)
                .hashedPassword(passwordHasher.hash(password))
                .role(role)
                .createdAt(Instant.now())
                .build();

        user = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                user.getUserId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        ));

        return user;
    }

    public String authenticateUser(String email, String password) {
        // Simple authentication check. JWT token generation is implied.
        return userRepository.findByEmail(email)
                .filter(user -> passwordHasher.matches(password, user.getHashedPassword()))
                .map(user -> "dummy-jwt-token-for-" + user.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    public User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserRole getUserRole(String userId) {
        return findUserById(userId).getRole();
    }
}
