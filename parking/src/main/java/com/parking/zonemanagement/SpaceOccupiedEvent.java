package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record SpaceOccupiedEvent(
    UUID spaceId,
    UUID zoneId,
    Long userId,
    ChargingPoint chargingPoint,
    Instant occurredAt
) {}
