package com.parking.reservation.internal;

import com.parking.reservation.Reservation;
import com.parking.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.spaceId = :spaceId " +
            "AND r.status IN :blockingStatuses " +
            "AND r.reservedFrom < :until " +
            "AND r.reservedUntil > :from")
    boolean existsOverlappingReservation(
            @Param("spaceId") UUID spaceId,
            @Param("from") Instant from,
            @Param("until") Instant until,
            @Param("blockingStatuses") List<ReservationStatus> blockingStatuses
    );
}