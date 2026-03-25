package com.parking.zonemanagement;

import java.time.Instant;

public record SpaceOccupiedEvent(
    String spaceId,
    String zoneId,
    String userId,
    ChargingPoint hasChargingPoint,
    Instant occurredAt
) {}
