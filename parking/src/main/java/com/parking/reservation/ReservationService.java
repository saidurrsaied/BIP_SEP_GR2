package com.parking.reservation;

import com.parking.reservation.internal.ReservationRepository;
import com.parking.reservation.internal.ReservationValidator;
import com.parking.zonemanagement.ParkingSpace;
import com.parking.zonemanagement.SpaceStatus;
import com.parking.zonemanagement.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationValidator reservationValidator;
    private final ZoneService zoneService; // External module dependency
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Reservation placeReservation(String userId, String spaceId, Instant from, Instant until) {
        reservationValidator.validate(userId, spaceId, from, until);

        ParkingSpace space = zoneService.getSpaceById(spaceId);
        if (space.getStatus() != SpaceStatus.FREE) {
            throw new RuntimeException("Space is not available for reservation");
        }

        zoneService.updateSpaceStatus(spaceId, SpaceStatus.RESERVED);

        Reservation reservation = Reservation.builder()
                .reservationId(UUID.randomUUID().toString())
                .userId(userId)
                .spaceId(spaceId)
                .zoneId(space.getZoneId())
                .status(ReservationStatus.CONFIRMED)
                .reservedFrom(from)
                .reservedUntil(until)
                .createdAt(Instant.now())
                .build();

        reservation = reservationRepository.save(reservation);

        eventPublisher.publishEvent(new ReservationPlacedEvent(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getSpaceId(),
                reservation.getZoneId(),
                reservation.getReservedFrom(),
                reservation.getReservedUntil(),
                reservation.getCreatedAt()
        ));

        return reservation;
    }

    @Transactional
    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        zoneService.updateSpaceStatus(reservation.getSpaceId(), SpaceStatus.FREE);

        eventPublisher.publishEvent(new ReservationCancelledEvent(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getSpaceId(),
                Instant.now()
        ));
    }

    @Transactional
    public void completeReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        long durationMinutes = Duration.between(reservation.getReservedFrom(), reservation.getReservedUntil()).toMinutes();

        eventPublisher.publishEvent(new ReservationCompletedEvent(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getSpaceId(),
                reservation.getZoneId(),
                durationMinutes,
                Instant.now()
        ));
    }

    public Reservation getReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public List<Reservation> getReservationsForUser(String userId) {
        return reservationRepository.findByUserId(userId);
    }
}
