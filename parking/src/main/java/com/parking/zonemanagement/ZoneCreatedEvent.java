package com.parking.zonemanagement;

import java.time.Instant;

public record ZoneCreatedEvent(
    Long zoneId,
    String name,
    Instant occurredAt
) {}
