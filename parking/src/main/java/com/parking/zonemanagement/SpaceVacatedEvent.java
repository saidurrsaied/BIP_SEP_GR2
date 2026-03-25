package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record SpaceVacatedEvent(
    UUID spaceId,
    UUID zoneId,
    Long userId,
    ChargingPoint chargingPoint,
    long occupationDurationMinutes,
    Instant occurredAt
) {}
