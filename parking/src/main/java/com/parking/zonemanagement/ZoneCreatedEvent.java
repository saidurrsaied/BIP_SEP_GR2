package com.parking.zonemanagement;

import java.time.Instant;

public record ZoneCreatedEvent(
    String zoneId,
    String name,
    Instant occurredAt
) {}
