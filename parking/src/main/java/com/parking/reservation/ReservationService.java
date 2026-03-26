package com.parking.reservation;

import com.parking.exception.ResourceNotFoundException;
import com.parking.reservation.internal.ReservationRepository;
import com.parking.reservation.internal.ReservationValidator;
import com.parking.usermanagement.UserService;
import com.parking.zonemanagement.CheckSpaceIsFreeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    public void checkReservationSpace(Long userId, UUID spaceId, Instant from, Instant until) {
        String userEmail = userService.findUserById(userId).getEmail();
        eventPublisher.publishEvent(new CheckSpaceIsFreeEvent(spaceId, from, until, userId, userEmail, Instant.now()));
    }

    @ApplicationModuleListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Reservation placeReservation(ReservationConfirmedEvent event) {
        reservationValidator.validate(event.userId(), event.spaceId(), event.from(), event.until());

        Reservation reservation = Reservation.builder()
                .userId(event.userId())
                .userEmail(event.userEmail())
                .spaceId(event.spaceId())
                .status(ReservationStatus.CONFIRMED)
                .reservedFrom(event.from())
                .reservedUntil(event.until())
                .createdAt(Instant.now())
                .build();

        reservation = reservationRepository.save(reservation);

        eventPublisher.publishEvent(new ReservationPlacedEvent(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getUserEmail(),
                reservation.getSpaceId(),
                reservation.getReservedFrom(),
                reservation.getReservedUntil(),
                reservation.getCreatedAt()
        ));

        return reservation;
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        eventPublisher.publishEvent(new ReservationCancelledEvent(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getUserEmail(),
                reservation.getSpaceId(),
                Instant.now()
        ));
    }

    @Transactional
    public void completeReservation(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        long durationMinutes = Duration.between(reservation.getReservedFrom(), reservation.getReservedUntil()).toMinutes();

        eventPublisher.publishEvent(new ReservationCompletedEvent(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getUserEmail(),
                reservation.getSpaceId(),
                durationMinutes,
                Instant.now()
        ));
    }

    @Transactional
    public Reservation updateReservation(Long reservationId, UUID newSpaceId, Instant newFrom, Instant newUntil) {
        Reservation reservation = getReservation(reservationId);
        Long userId = reservation.getUserId();

        reservationValidator.validate(userId, newSpaceId, newFrom, newUntil);

        eventPublisher.publishEvent(new ReservationCancelledEvent(
                reservation.getReservationId(),
                userId,
                reservation.getUserEmail(),
                reservation.getSpaceId(),
                Instant.now()
        ));

        reservation.setSpaceId(newSpaceId);
        reservation.setReservedFrom(newFrom);
        reservation.setReservedUntil(newUntil);
        reservation.setStatus(ReservationStatus.PENDING);

        reservation = reservationRepository.save(reservation);

        eventPublisher.publishEvent(new CheckSpaceIsFreeEvent(
                newSpaceId,
                newFrom,
                newUntil,
                userId,
                reservation.getUserEmail(),
                Instant.now()
        ));

        return reservation;
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found"));
    }

    public List<Reservation> getReservationsForUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
}
