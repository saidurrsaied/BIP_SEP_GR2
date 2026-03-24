package com.parking.usermanagement;

import java.time.Instant;

public record UserRegisteredEvent(
    String userId,
    String email,
    UserRole role,
    Instant occurredAt
) {}
