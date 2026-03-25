package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record ZoneCreatedEvent(
    UUID zoneId,
    String name,
    Instant occurredAt
) {}
