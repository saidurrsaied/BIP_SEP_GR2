package com.parking.reservation;

import java.time.Instant;
import java.util.UUID;

public record ReservationCompletedEvent(
    Long reservationId,
    Long userId,
    String userEmail,
    UUID spaceId,
    long durationMinutes,
    Instant occurredAt
) {}
