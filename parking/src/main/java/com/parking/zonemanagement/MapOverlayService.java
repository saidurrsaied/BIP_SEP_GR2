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
                .zoneId(zone.getZoneId())
                .name(zone.getName())
                .latitude(zone.getLatitude())
                .longitude(zone.getLongitude())
                .spaces(zone.getSpaces().stream()
                        .map(s -> SpaceData.builder()
                                .spaceId(s.getSpaceId())
                                .status(s.getStatus())
                                .chargingPoint(s.getChargingPoint())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @Builder
    public static class MapData {
        private Long zoneId;
        private String name;
        private double latitude;
        private double longitude;
        private List<SpaceData> spaces;
    }

    @Getter
    @Builder
    public static class SpaceData {
        private Long spaceId;
        private SpaceStatus status;
        private ChargingPoint chargingPoint;
    }
}
