package com.parking.notification;

import com.parking.billing.PaymentProcessedEvent;
import com.parking.notification.internal.NotificationProviderClient;
import com.parking.notification.internal.NotificationRecord;
import com.parking.notification.internal.NotificationRepository;
import com.parking.reservation.ReservationCancelledEvent;
import com.parking.reservation.ReservationPlacedEvent;
import com.parking.zonemanagement.SpaceOccupiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProviderClient notificationProviderClient;

    @ApplicationModuleListener
    public void onReservationPlaced(ReservationPlacedEvent event) {
        String message = "Reservation confirmed for space " + event.spaceId() + " from " + event.from() + " to " + event.until();
        boolean success = notificationProviderClient.sendEmail(event.userId(), "Reservation Confirmed", message);
        saveRecord("ReservationPlacedEvent", event.userId(), "EMAIL", success ? "SENT" : "FAILED");
    }

    @ApplicationModuleListener
    public void onReservationCancelled(ReservationCancelledEvent event) {
        String message = "Reservation " + event.reservationId() + " has been cancelled.";
        boolean success = notificationProviderClient.sendEmail(event.userId(), "Reservation Cancelled", message);
        saveRecord("ReservationCancelledEvent", event.userId(), "EMAIL", success ? "SENT" : "FAILED");
    }

    @ApplicationModuleListener
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        String message = "Payment of " + event.totalAmountCents() + " cents processed for invoice " + event.invoiceId();
        boolean success = notificationProviderClient.sendEmail(event.userId(), "Payment Receipt", message);
        saveRecord("PaymentProcessedEvent", event.userId(), "EMAIL", success ? "SENT" : "FAILED");
    }

    @ApplicationModuleListener
    public void onSpaceOccupied(SpaceOccupiedEvent event) {
        // Optional: alert users about changing availability
        if (event.userId() != null) {
            String message = "You have successfully occupied space " + event.spaceId();
            notificationProviderClient.sendSms(event.userId(), message);
            saveRecord("SpaceOccupiedEvent", event.userId(), "SMS", "SENT");
        }
    }

    private void saveRecord(String eventType, String userId, String channel, String status) {
        notificationRepository.save(NotificationRecord.builder()
                .notificationId(UUID.randomUUID().toString())
                .event(eventType)
                .userId(userId)
                .sentAt(Instant.now())
                .channel(channel)
                .status(status)
                .build());
    }
}
