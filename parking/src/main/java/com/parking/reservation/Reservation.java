package com.parking.reservation;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "res_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private Long userId; // Plain ID reference
    private UUID spaceId; // Plain ID reference
    private UUID zoneId; // Plain ID reference

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Instant reservedFrom;
    private Instant reservedUntil;
    private Instant createdAt;
}
