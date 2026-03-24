package com.parking.zonemanagement;

import java.time.Instant;

public record SpaceOccupiedEvent(
    Long spaceId,
    Long zoneId,
    Long userId,
    HasChargingPoint hasChargingPoint,
    Instant occurredAt
) {}
