package com.parking.zonemanagement;

import java.time.Instant;

public record SpaceStatusChangedEvent(
    Long spaceId,
    Long zoneId,
    SpaceStatus oldStatus,
    SpaceStatus newStatus,
    Instant occurredAt
) {}
