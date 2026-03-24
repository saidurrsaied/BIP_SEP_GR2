package com.parking.reservation.internal;

import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class ReservationValidator {
    public void validate(String userId, String spaceId, Instant from, Instant until) {
        if (from.isAfter(until)) {
            throw new RuntimeException("Reservation start must be before end");
        }
        if (from.isBefore(Instant.now())) {
            throw new RuntimeException("Reservation cannot be in the past");
        }
        // Additional business rules would go here
    }
}
