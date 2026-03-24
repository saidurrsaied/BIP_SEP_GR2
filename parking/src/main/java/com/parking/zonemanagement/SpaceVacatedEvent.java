package com.parking.zonemanagement;

import java.time.Instant;

public record SpaceVacatedEvent(
    Long spaceId,
    Long zoneId,
    Long userId,
    HasChargingPoint hasChargingPoint,
    long occupationDurationMinutes,
    Instant occurredAt
) {}
