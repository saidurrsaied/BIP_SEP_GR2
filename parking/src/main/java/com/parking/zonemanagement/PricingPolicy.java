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
    private int chargingRatePerKwhCents;
    private int fastChargingRatePerKwhCents;
    private String currency;
}
