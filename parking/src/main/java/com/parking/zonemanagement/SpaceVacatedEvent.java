package com.parking.zonemanagement;

import java.time.Instant;

public record SpaceVacatedEvent(
    String spaceId,
    String zoneId,
    String userId,
    ChargingPoint chargingPoint,
    long occupationDurationMinutes,
    Instant occurredAt
) {}
