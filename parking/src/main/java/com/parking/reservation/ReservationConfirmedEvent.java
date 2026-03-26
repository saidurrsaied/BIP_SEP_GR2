package com.parking.reservation;

import java.time.Instant;
import java.util.UUID;

public record ReservationConfirmedEvent(
        UUID spaceId,
        Instant from,
        Instant until,
        Long userId,
        String userEmail,
        Instant occurredAt
) {}
