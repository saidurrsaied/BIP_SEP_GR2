package com.parking.reservation.internal;

import com.parking.exception.BusinessException;
import com.parking.reservation.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    // 1. Inject the repository instance
    private final ReservationRepository reservationRepository;

    public void validate(Long userId, UUID spaceId, Instant from, Instant until) {
        if (from.isAfter(until)) {
            throw new BusinessException("Reservation start time must be before end time");
        }
        if (from.isBefore(Instant.now())) {
            throw new BusinessException("Reservation start time cannot be in the past");
        }

        // 2. Define the blocking statuses
        List<ReservationStatus> blockingStatuses = List.of(ReservationStatus.CONFIRMED);

        // 3. Call the method on the injected instance (lowercase 'r')
        boolean hasOverlap = reservationRepository.existsOverlappingReservation(
                spaceId, from, until, blockingStatuses
        );

        if (hasOverlap) {
            throw new BusinessException("The space is already reserved for the requested time slot.");
        }
    }
}