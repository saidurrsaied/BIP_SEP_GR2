package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record SpaceVacatedEvent(
    UUID spaceId,
    Long userId,
    String userEmail,
    ChargingPoint chargingPoint,
    long occupationDurationMinutes,
    PricingPolicy pricingPolicy,
    Instant occurredAt
) {}
