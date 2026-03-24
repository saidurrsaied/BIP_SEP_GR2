package com.parking.zonemanagement;

import java.time.Instant;

public record SpaceStatusChangedEvent(
    String spaceId,
    String zoneId,
    SpaceStatus oldStatus,
    SpaceStatus newStatus,
    Instant occurredAt
) {}
