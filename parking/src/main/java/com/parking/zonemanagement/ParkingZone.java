package com.parking.zonemanagement;

import com.parking.zonemanagement.PricingPolicy;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    private Long zoneId;

    private String name;
    private String city;
    private String address;
    private double latitude;
    private double longitude;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "zoneId")
    private List<ParkingSpace> spaces;

    @Embedded
    private PricingPolicy pricingPolicy;
}
