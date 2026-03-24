package com.parking.zonemanagement.internal;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class MapIntegrationClient {
    public Map<String, Object> getCoordinates(String address) {
        // Placeholder for external mapping service call
        return Map.of("lat", 51.5136, "lng", 7.4653);
    }
}
