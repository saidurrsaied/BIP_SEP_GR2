package com.parking.reservation;

import java.time.Instant;

public record ReservationCancelledEvent(
    Long reservationId,
    Long userId,
    Long spaceId,
    Instant occurredAt
) {}
