package com.parking.usermanagement.internal;

import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    public String hash(String rawPassword) {
        // Placeholder for hashing until security dependency is correctly resolved or for simplicity
        return "hashed-" + rawPassword;
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return ("hashed-" + rawPassword).equals(encodedPassword);
    }
}
