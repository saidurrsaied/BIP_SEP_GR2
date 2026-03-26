package com.parking.reservation;

import java.time.Instant;
import java.util.UUID;

public record ReservationCancelledEvent(
    Long reservationId,
    Long userId,
    String userEmail,
    UUID spaceId,
    Instant occurredAt
) {}
