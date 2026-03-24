package com.parking.usermanagement;

import com.parking.exception.BusinessException;
import com.parking.exception.ConflictException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.usermanagement.internal.PasswordHasher;
import com.parking.usermanagement.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public User registerUser(String email, String password, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("An account with email '" + email + "' already exists");
        }

        User user = User.builder()
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
                .orElseThrow(() -> new BusinessException("Invalid email or password"));
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    public UserRole getUserRole(Long userId) {
        return findUserById(userId).getRole();
    }
}
