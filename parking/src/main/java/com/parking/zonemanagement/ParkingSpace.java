package com.parking.zonemanagement;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "zm_spaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spaceId;

    private Long zoneId;

    @Enumerated(EnumType.STRING)
    private SpaceStatus status;

    @Enumerated(EnumType.STRING)
    private ChargingPoint chargingPoint;

    private String level;
    private String spaceNumber;
}
