package com.parking.zonemanagement;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "zm_spaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID spaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id",  nullable = false)
    private ParkingZone zone;

    @Enumerated(EnumType.STRING)
    private SpaceStatus status;

    @Enumerated(EnumType.STRING)
    private ChargingPoint chargingPoint;

    private String level;
    private String spaceNumber;
}
