package com.parking.reservation.internal;

import com.parking.exception.BusinessException;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Component
public class ReservationValidator {
    public void validate(Long userId, UUID spaceId, Instant from, Instant until) {
        if (from.isAfter(until)) {
            throw new BusinessException("Reservation start time must be before end time");
        }
        if (from.isBefore(Instant.now())) {
            throw new BusinessException("Reservation start time cannot be in the past");
        }
        // Additional business rules would go here

        // TODO : is there already a reservation for that space at that time?
            // No one can be parked at the time of making the reservation of that spot
            // No one can have a reservation interfering with the time slot that the next person wants
    }
}
