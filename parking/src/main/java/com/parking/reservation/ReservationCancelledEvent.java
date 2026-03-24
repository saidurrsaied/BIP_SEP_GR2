package com.parking.reservation;

import java.time.Instant;

public record ReservationCancelledEvent(
    String reservationId,
    String userId,
    String spaceId,
    Instant occurredAt
) {}
