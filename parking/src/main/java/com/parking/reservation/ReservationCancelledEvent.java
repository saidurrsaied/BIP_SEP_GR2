package com.parking.reservation;

import java.time.Instant;
import java.util.UUID;

public record ReservationCancelledEvent(
    Long reservationId,
    Long userId,
    UUID spaceId,
    Instant occurredAt
) {}
