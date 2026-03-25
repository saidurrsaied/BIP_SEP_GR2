package com.parking.zonemanagement;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PricingPolicy {
    private int hourlyRateCents;
    private int slowMultiplier = 2;
    private int fastMultiplier = 4;
}
