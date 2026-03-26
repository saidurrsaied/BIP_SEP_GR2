package com.parking.reservation;

import java.time.Instant;
import java.util.UUID;

public record ReservationPlacedEvent(
    Long reservationId,
    Long userId,
    String userEmail,
    UUID spaceId,
    Instant from,
    Instant until,
    Instant occurredAt
) {}
