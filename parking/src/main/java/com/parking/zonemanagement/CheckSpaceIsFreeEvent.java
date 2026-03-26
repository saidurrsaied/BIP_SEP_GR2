package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record CheckSpaceIsFreeEvent(
        UUID spaceId,
        Instant from,
        Instant until,
        Long userId,
        String userEmail,
        Instant occurredAt
) {}