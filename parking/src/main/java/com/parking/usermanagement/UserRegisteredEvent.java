package com.parking.usermanagement;

import java.time.Instant;

public record UserRegisteredEvent(
    Long userId,
    String email,
    String numberplate,
    UserRole role,
    Instant occurredAt
) {}
