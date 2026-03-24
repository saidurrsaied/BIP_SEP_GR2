package com.parking.reservation;

import java.time.Instant;

public record ReservationPlacedEvent(
    Long reservationId,
    Long userId,
    Long spaceId,
    Long zoneId,
    Instant from,
    Instant until,
    Instant occurredAt
) {}
