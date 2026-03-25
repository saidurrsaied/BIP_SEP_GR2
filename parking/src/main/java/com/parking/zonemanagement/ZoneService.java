package com.parking.zonemanagement;

import com.parking.zonemanagement.internal.SpaceRepository;
import com.parking.zonemanagement.internal.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final SpaceRepository spaceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ParkingZone createZone(String name, String address, String city, double lat, double lng, PricingPolicy pricingPolicy) {
        ParkingZone zone = ParkingZone.builder()
                .zoneId(UUID.randomUUID())
                .name(name)
                .address(address)
                .city(city)
                .latitude(lat)
                .longitude(lng)
                .pricingPolicy(pricingPolicy)
                .spaces(new ArrayList<>())
                .build();

        zone = zoneRepository.save(zone);

        eventPublisher.publishEvent(new ZoneCreatedEvent(zone.getZoneId(), zone.getName(), Instant.now()));

        return zone;
    }

    public ParkingZone getZoneById(UUID zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
    }

    @Transactional
    public ParkingZone updateZone(UUID zoneId, String name, String address, String city, double lat, double lng, PricingPolicy pricingPolicy) {
        ParkingZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        zone.setName(name);
        zone.setAddress(address);
        zone.setCity(city);
        zone.setLatitude(lat);
        zone.setLongitude(lng);
        zone.setPricingPolicy(pricingPolicy);

        return zoneRepository.save(zone);
    }

    @Transactional
    public void deleteZone(UUID zoneId) {
        ParkingZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        zoneRepository.delete(zone);
    }

    @Transactional
    public ParkingSpace addSpaceToZone(UUID zoneId, ChargingPoint chargingPoint, String level, String spaceNumber) {
        ParkingZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        ParkingSpace space = ParkingSpace.builder()
                .spaceId(UUID.randomUUID())
                .zone(zone)
                .status(SpaceStatus.FREE)
                .chargingPoint(chargingPoint)
                .level(level)
                .spaceNumber(spaceNumber)
                .build();

        zone.getSpaces().add(space);
        zoneRepository.save(zone);

        return space;
    }

    @Transactional
    public void updateSpaceStatus(UUID spaceId, SpaceStatus newStatus) {
        ParkingSpace space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        SpaceStatus oldStatus = space.getStatus();
        space.setStatus(newStatus);
        spaceRepository.save(space);

        eventPublisher.publishEvent(new SpaceStatusChangedEvent(
                space.getSpaceId(),
                space.getZone().getZoneId(),
                oldStatus,
                newStatus,
                Instant.now()
        ));
    }

    public List<ParkingSpace> getAvailableSpaces(UUID zoneId) {
        return spaceRepository.findByZoneId(zoneId).stream()
                .filter(s -> s.getStatus() == SpaceStatus.FREE)
                .toList();
    }

    public ParkingSpace getSpaceById(UUID spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new RuntimeException("Space not found"));
    }

    public PricingPolicy getPricingPolicy(UUID zoneId) {
        return zoneRepository.findById(zoneId)
                .map(ParkingZone::getPricingPolicy)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
    }

    public List<ParkingZone> getAllZones() {
        return zoneRepository.findAll();
    }
}
