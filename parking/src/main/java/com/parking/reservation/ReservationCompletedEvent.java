package com.parking.reservation;

import java.time.Instant;

public record ReservationCompletedEvent(
    Long reservationId,
    Long userId,
    Long spaceId,
    Long zoneId,
    long durationMinutes,
    Instant occurredAt
) {}
