package com.parking.zonemanagement;

import com.parking.exception.BusinessException;
import com.parking.zonemanagement.internal.OccupationRecord;
import com.parking.zonemanagement.internal.OccupationRepository;
import com.parking.zonemanagement.internal.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OccupationService {

    private final OccupationRepository occupationRepository;
    private final SpaceRepository spaceRepository;
    private final ZoneService zoneService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void markSpaceOccupied(Long spaceId, Long userId) {
        ParkingSpace space = zoneService.getSpaceById(spaceId);

        zoneService.updateSpaceStatus(spaceId, SpaceStatus.OCCUPIED);

        OccupationRecord record = OccupationRecord.builder()
                .spaceId(spaceId)
                .userId(userId)
                .startTime(Instant.now())
                .hasChargingPoint(space.getChargingPoint())
                .build();

        occupationRepository.save(record);

        eventPublisher.publishEvent(new SpaceOccupiedEvent(
                spaceId,
                space.getZoneId(),
                userId,
                space.getChargingPoint(),
                Instant.now()
        ));
    }

    @Transactional
    public OccupationRecord markSpaceVacated(Long spaceId) {
        OccupationRecord record = occupationRepository.findBySpaceIdAndEndTimeIsNull(spaceId)
                .orElseThrow(() -> new BusinessException("Space " + spaceId + " is not currently occupied"));

        record.setEndTime(Instant.now());
        occupationRepository.save(record);

        zoneService.updateSpaceStatus(spaceId, SpaceStatus.FREE);

        ParkingSpace space = zoneService.getSpaceById(spaceId);

        long durationMinutes = Duration.between(record.getStartTime(), record.getEndTime()).toMinutes();

        eventPublisher.publishEvent(new SpaceVacatedEvent(
                spaceId,
                space.getZoneId(),
                record.getUserId(),
                record.getHasChargingPoint(),
                durationMinutes,
                Instant.now()
        ));

        return record;
    }

    public OccupationRecord getCurrentOccupancy(Long spaceId) {
        return occupationRepository.findBySpaceIdAndEndTimeIsNull(spaceId).orElse(null);
    }
}
