package com.parking.zonemanagement;

import java.time.Instant;

public record SpaceOccupiedEvent(
    String spaceId,
    String zoneId,
    String userId,
    HasChargingPoint hasChargingPoint,
    Instant occurredAt
) {}
