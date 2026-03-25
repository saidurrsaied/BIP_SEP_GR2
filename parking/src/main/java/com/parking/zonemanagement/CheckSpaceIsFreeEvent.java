package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record CheckSpaceIsFreeEvent(
        UUID spaceId,
        Instant from,
        Instant until,
        Long userId,
        Instant occurredAt
) {}