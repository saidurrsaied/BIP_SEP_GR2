package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record SpaceOccupiedEvent(
    UUID spaceId,
    UUID zoneId,
    Long userId,
    String userEmail,
    ChargingPoint chargingPoint,
    Instant occurredAt
) {}
