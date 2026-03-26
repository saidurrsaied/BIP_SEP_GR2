package com.parking.zonemanagement;

import com.parking.reservation.ReservationConfirmedEvent;
import com.parking.zonemanagement.internal.SpaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @ApplicationModuleListener
    public void onCheckSpaceIsFreeEvent(CheckSpaceIsFreeEvent event) {
        var space = spaceRepository.findBySpaceId(event.spaceId());
        if (space.isPresent()) {
            eventPublisher.publishEvent(new ReservationConfirmedEvent(
                    space.get().getSpaceId(),
                    event.from(),
                    event.until(),
                    event.userId(),
                    Instant.now()
            ));
        }
    }
}
