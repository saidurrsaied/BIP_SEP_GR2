package com.parking.zonemanagement;

import com.parking.reservation.ReservationCancelledEvent; // Zorg dat deze events publiek zijn!
import com.parking.reservation.ReservationConfirmedEvent;
import com.parking.zonemanagement.internal.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SpaceService {
    private final SpaceRepository spaceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @ApplicationModuleListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCheckSpaceIsFreeEvent(CheckSpaceIsFreeEvent event) {
        var spaceOptional = spaceRepository.findBySpaceId(event.spaceId());

        if (spaceOptional.isPresent()) {
            ParkingSpace space = spaceOptional.get();

            // 1. EXTRA CHECK: Is de plek nog steeds echt FREE?
            if (space.getStatus() == SpaceStatus.FREE) {

                // 2. DE BELANGRIJKE STAP: Verander de status en SLA OP
                space.setStatus(SpaceStatus.RESERVED);
                spaceRepository.save(space);

                // 3. Nu pas sturen we de bevestiging naar de Reservation module
                eventPublisher.publishEvent(new ReservationConfirmedEvent(
                        space.getSpaceId(),
                        event.from(),
                        event.until(),
                        event.userId(),
                        Instant.now()
                ));

                System.out.println("DEBUG: Space " + space.getSpaceNumber() + " is nu gereserveerd in de DB.");
            }
        }
    }

    // NIEUW: Zorg dat de plek weer vrij komt als de reservering stopt/geannuleerd wordt!
    @ApplicationModuleListener
    public void onReservationCancelled(ReservationCancelledEvent event) {
        spaceRepository.findBySpaceId(event.spaceId()).ifPresent(space -> {
            space.setStatus(SpaceStatus.FREE);
            spaceRepository.save(space);
            System.out.println("DEBUG: Space " + space.getSpaceNumber() + " is weer vrijgegeven.");
        });
    }
}
