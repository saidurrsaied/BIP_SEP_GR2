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
    private String spaceId;

    private String zoneId;

    @Enumerated(EnumType.STRING)
    private SpaceStatus status;

    @Enumerated(EnumType.STRING)
    private HasChargingPoint hasChargingPoint;

    private String level;
    private String spaceNumber;
}
