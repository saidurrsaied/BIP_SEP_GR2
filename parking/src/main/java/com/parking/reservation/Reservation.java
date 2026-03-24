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
    private String reservationId;

    private String userId; // Plain ID reference
    private String spaceId; // Plain ID reference
    private String zoneId; // Plain ID reference

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Instant reservedFrom;
    private Instant reservedUntil;
    private Instant createdAt;
}
