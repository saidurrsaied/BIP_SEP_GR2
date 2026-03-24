package com.parking.zonemanagement.internal;

import com.parking.zonemanagement.HasChargingPoint;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "zm_occupation_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupationRecord {
    @Id
    private String recordId;

    private String spaceId;
    private String userId; // Nullable for walk-ins
    private Instant startTime;
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    private HasChargingPoint hasChargingPoint;
}
