package com.parking.reservation;

import com.parking.exception.BusinessException;
import com.parking.exception.ResourceNotFoundException;
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
    public Reservation placeReservation(Long userId, UUID spaceId, Instant from, Instant until) {
        reservationValidator.validate(userId, spaceId, from, until);

        ParkingSpace space = zoneService.getSpaceById(spaceId);
        if (space.getStatus() != SpaceStatus.FREE) {
            throw new BusinessException("Space " + spaceId + " is not available for reservation (current status: " + space.getStatus() + ")");
        }

        zoneService.updateSpaceStatus(spaceId, SpaceStatus.RESERVED);

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .spaceId(spaceId)
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

        zoneService.updateSpaceStatus(reservation.getSpaceId(), SpaceStatus.FREE);

        eventPublisher.publishEvent(new ReservationCancelledEvent(
                reservation.getReservationId(),
                reservation.getUserId(),
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
                reservation.getSpaceId(),
                durationMinutes,
                Instant.now()
        ));
    }

    @Transactional
    public Reservation updateReservation(Long reservationId, UUID newSpaceId, Instant newFrom, Instant newUntil){
        Reservation oldReservation = getReservation(reservationId);
        Long userId = oldReservation.getUserId();
        Instant originalCreatedAt = oldReservation.getCreatedAt();

        reservationValidator.validate(userId, newSpaceId, newFrom, newUntil);

        cancelReservation(reservationId);
        ParkingSpace space = zoneService.getSpaceById(newSpaceId);
        if (space.getStatus() != SpaceStatus.FREE){
            throw new BusinessException("Space " + newSpaceId + " is not available");
        }

        Reservation updatedReservation= Reservation.builder().reservationId(reservationId)
                .userId(userId)
                .spaceId(newSpaceId)
                .status(ReservationStatus.CONFIRMED)
                .reservedFrom(newFrom)
                .reservedUntil(newUntil)
                .createdAt(originalCreatedAt)
                .build();

        updatedReservation = reservationRepository.save(updatedReservation);

        eventPublisher.publishEvent(new ReservationPlacedEvent(
                updatedReservation.getReservationId(),
                updatedReservation.getUserId(),
                updatedReservation.getSpaceId(),
                updatedReservation.getReservedFrom(),
                updatedReservation.getReservedUntil(),
                updatedReservation.getCreatedAt()
        ));

        return updatedReservation;
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found"));
    }

    public List<Reservation> getReservationsForUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
}
