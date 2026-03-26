package com.parking.zonemanagement;

import com.parking.exception.ResourceNotFoundException;
import com.parking.zonemanagement.internal.ZoneRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapOverlayService {

    private final ZoneRepository zoneRepository;

    @Transactional(readOnly = true)
    public MapData getEnrichedMapData(Long zoneId) {
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

    private MapData mapToData(ParkingZone zone) {
        return MapData.builder()
                // We stoppen de zone data nu in een genest 'zone' object, exact zoals Svelte het wil
                .zone(ZoneDto.builder()
                        .id(zone.getZoneId()) // We noemen het 'id' voor de frontend
                        .name(zone.getName())
                        .address(zone.getAddress())
                        .city(zone.getCity())
                        .latitude(zone.getLatitude())
                        .longitude(zone.getLongitude())
                        .pricingPolicy(zone.getPricingPolicy())
                        .build())
                .spaces(zone.getSpaces().stream()
                        .map(s -> SpaceData.builder()
                                .id(s.getSpaceId()) // We noemen het 'id' voor de frontend
                                .status(s.getStatus())
                                .hasChargingPoint(s.getHasChargingPoint())
                                .level(s.getLevel())
                                .spaceNumber(s.getSpaceNumber())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // --- DTO Classes die bepalen hoe de JSON eruit komt te zien ---

    @Getter
    @Builder
    public static class MapData {
        private ZoneDto zone;
        private List<SpaceData> spaces;
    }

    @Getter
    @Builder
    public static class ZoneDto {
        private Long id;
        private String name;
        private String address;
        private String city;
        private double latitude;
        private double longitude;
        private PricingPolicy pricingPolicy; // Zorg dat deze class bestaat in Java!
    }

    @Getter
    @Builder
    public static class SpaceData {
        private Long id;
        private SpaceStatus status;
        private HasChargingPoint hasChargingPoint;
        private String level;
        private String spaceNumber;
    }
}
