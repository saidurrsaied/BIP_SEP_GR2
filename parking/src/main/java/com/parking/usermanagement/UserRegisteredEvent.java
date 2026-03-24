package com.parking.usermanagement;

import java.time.Instant;

public record UserRegisteredEvent(
    Long userId,
    String email,
    UserRole role,
    Instant occurredAt
) {}
