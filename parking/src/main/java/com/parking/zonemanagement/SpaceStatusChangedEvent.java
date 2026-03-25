package com.parking.zonemanagement;

import java.time.Instant;
import java.util.UUID;

public record SpaceStatusChangedEvent(
    UUID spaceId,
    UUID zoneId,
    SpaceStatus oldStatus,
    SpaceStatus newStatus,
    Instant occurredAt
) {}
