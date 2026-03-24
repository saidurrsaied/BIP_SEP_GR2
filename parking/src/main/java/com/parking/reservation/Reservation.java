package com.parking.reservation;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

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
    private Long spaceId; // Plain ID reference
    private Long zoneId; // Plain ID reference

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Instant reservedFrom;
    private Instant reservedUntil;
    private Instant createdAt;
}
