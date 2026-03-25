package com.parking.billing.internal;

import com.parking.zonemanagement.PricingPolicy;
import org.springframework.stereotype.Component;

@Component
public class PriceCalculator {
    public long calculateParkingCost(long durationMinutes, PricingPolicy policy) {
        return (durationMinutes / 60 + (durationMinutes % 60 > 0 ? 1 : 0)) * policy.getHourlyRateCents();
    }

    public long calculateChargingCost(long kwh, PricingPolicy policy, boolean fastCharging) {
        return kwh * (fastCharging ? policy.getFastMultiplier() : policy.getSlowMultiplier());
    }
}
