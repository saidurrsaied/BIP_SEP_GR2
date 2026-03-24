package com.parking.reservation;

import java.time.Instant;

public record ReservationPlacedEvent(
    String reservationId,
    String userId,
    String spaceId,
    String zoneId,
    Instant from,
    Instant until,
    Instant occurredAt
) {}
