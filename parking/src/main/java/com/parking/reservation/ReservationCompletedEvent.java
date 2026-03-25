package com.parking.reservation;

import java.time.Instant;
import java.util.UUID;

public record ReservationCompletedEvent(
    Long reservationId,
    Long userId,
    UUID spaceId,
    UUID zoneId,
    long durationMinutes,
    Instant occurredAt
) {}
