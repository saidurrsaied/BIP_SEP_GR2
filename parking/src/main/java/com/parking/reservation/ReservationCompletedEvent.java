package com.parking.reservation;

import java.time.Instant;

public record ReservationCompletedEvent(
    String reservationId,
    String userId,
    String spaceId,
    String zoneId,
    long durationMinutes,
    Instant occurredAt
) {}
