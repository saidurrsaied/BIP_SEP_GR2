package com.parking.zonemanagement;

import com.parking.exception.ResourceNotFoundException;
import com.parking.zonemanagement.internal.ZoneRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapOverlayService {

    private final ZoneRepository zoneRepository;

    @Transactional(readOnly = true)
    public MapData getEnrichedMapData(UUID zoneId) {
        return zoneRepository.findById(zoneId)
                .map(this::mapToData)
                .orElseThrow(() -> new ResourceNotFoundException("Zone with id " + zoneId + " not found"));
    }

    @Transactional(readOnly = true)
    public List<MapData> getAllZonesMapData() {
        return zoneRepository.findAll().stream()
                .map(this::mapToData)
                .collect(Collectors.toList());
    }

    /**
     * Mapt de ParkingZone entiteit naar de MapData DTO voor de frontend.
     */
    private MapData mapToData(ParkingZone zone) {
        return MapData.builder()
                .zoneId(zone.getZoneId())
                .name(zone.getName())
                .address(zone.getAddress()) // Toegevoegd voor de lijstweergave
                .city(zone.getCity())       // Toegevoegd voor de lijstweergave
                .latitude(zone.getLatitude())
                .longitude(zone.getLongitude())
                .pricingPolicy(zone.getPricingPolicy()) // DIT WAS DE MISSING LINK!
                .spaces(zone.getSpaces().stream()
                        .map(s -> SpaceData.builder()
                                .spaceId(s.getSpaceId())
                                .status(s.getStatus())
                                .chargingPoint(s.getChargingPoint())
                                .level(s.getLevel())        // Ook handig voor de details
                                .spaceNumber(s.getSpaceNumber()) // Ook handig voor de details
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // --- DTO's (Data Transfer Objects) ---

    @Getter
    @Builder
    public static class MapData {
        private UUID zoneId;
        private String name;
        private String address;
        private String city;
        private double latitude;
        private double longitude;
        private PricingPolicy pricingPolicy; // Nu opgenomen in de DTO
        private List<SpaceData> spaces;
    }

    @Getter
    @Builder
    public static class SpaceData {
        private UUID spaceId;
        private SpaceStatus status;
        private ChargingPoint chargingPoint;
        private String level;
        private String spaceNumber;
    }
}