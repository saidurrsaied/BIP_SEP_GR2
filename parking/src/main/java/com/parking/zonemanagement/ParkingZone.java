package com.parking.zonemanagement;

import com.parking.zonemanagement.PricingPolicy;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "zm_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID zoneId;

    private String name;
    private String city;
    private String address;
    private double latitude;
    private double longitude;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ParkingSpace> spaces = new ArrayList<>();

    @Embedded
    private PricingPolicy pricingPolicy;
}
