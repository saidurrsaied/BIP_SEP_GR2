package com.parking.zonemanagement.internal;

import com.parking.zonemanagement.ChargingPoint;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "zm_occupation_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID recordId;

    private UUID spaceId;
    private Long userId; // Nullable for walk-ins
    private Instant startTime;
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    private ChargingPoint hasChargingPoint;
}
