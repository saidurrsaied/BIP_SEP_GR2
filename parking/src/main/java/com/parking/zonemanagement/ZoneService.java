package com.parking.zonemanagement;

import com.parking.exception.ResourceNotFoundException;
import com.parking.zonemanagement.PricingPolicy;
import com.parking.zonemanagement.internal.SpaceRepository;
import com.parking.zonemanagement.internal.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final SpaceRepository spaceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ParkingZone createZone(String name, String address, String city, double lat, double lng, PricingPolicy pricingPolicy) {
        ParkingZone zone = ParkingZone.builder()
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

    @Transactional
    public ParkingSpace addSpaceToZone(Long zoneId, HasChargingPoint hasChargingPoint, String level, String spaceNumber) {
        ParkingZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone with id " + zoneId + " not found"));

        ParkingSpace space = ParkingSpace.builder()
                .zoneId(zoneId)
                .status(SpaceStatus.FREE)
                .hasChargingPoint(hasChargingPoint)
                .level(level)
                .spaceNumber(spaceNumber)
                .build();

        zone.getSpaces().add(space);
        zoneRepository.save(zone);

        return space;
    }

    @Transactional
    public void updateSpaceStatus(Long spaceId, SpaceStatus newStatus) {
        ParkingSpace space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space with id " + spaceId + " not found"));

        SpaceStatus oldStatus = space.getStatus();
        space.setStatus(newStatus);
        spaceRepository.save(space);

        eventPublisher.publishEvent(new SpaceStatusChangedEvent(
                space.getSpaceId(),
                space.getZoneId(),
                oldStatus,
                newStatus,
                Instant.now()
        ));
    }

    public List<ParkingSpace> getAvailableSpaces(Long zoneId) {
        return spaceRepository.findByZoneId(zoneId).stream()
                .filter(s -> s.getStatus() == SpaceStatus.FREE)
                .toList();
    }

    public ParkingSpace getSpaceById(Long spaceId) {
        return spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space with id " + spaceId + " not found"));
    }

    @Transactional
    public ParkingSpace getSpaceByIdForUpdate(Long spaceId) {
        return spaceRepository.findByIdForUpdate(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space with id " + spaceId + " not found"));
    }

    public PricingPolicy getPricingPolicy(Long zoneId) {
        return zoneRepository.findById(zoneId)
                .map(ParkingZone::getPricingPolicy)
                .orElseThrow(() -> new ResourceNotFoundException("Zone with id " + zoneId + " not found"));
    }
}
