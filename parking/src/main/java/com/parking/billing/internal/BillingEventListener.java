package com.parking.billing.internal;

import com.parking.billing.*;
import com.parking.reservation.ReservationCompletedEvent;
import com.parking.zonemanagement.HasChargingPoint;
import com.parking.zonemanagement.SpaceVacatedEvent;
import com.parking.zonemanagement.ZoneService;
import com.parking.zonemanagement.PricingPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BillingEventListener {

    private final InvoiceRepository invoiceRepository;
    private final PriceCalculator priceCalculator;
    private final PaymentGatewayClient paymentGatewayClient;
    private final ZoneService zoneService;
    private final ApplicationEventPublisher eventPublisher;

    @ApplicationModuleListener
    public void onReservationCompleted(ReservationCompletedEvent event) {
        PricingPolicy policy = zoneService.getPricingPolicy(event.zoneId());
        long cost = priceCalculator.calculateParkingCost(event.durationMinutes(), policy);

        BillingItem item = BillingItem.builder()
                .type(BillingItemType.PARKING)
                .description("Parking fee for " + event.durationMinutes() + " minutes")
                .amountCents(cost)
                .durationMinutes((int) event.durationMinutes())
                .build();

        Invoice invoice = Invoice.builder()
                .userId(event.userId())
                .reservationId(event.reservationId())
                .items(new ArrayList<>(List.of(item)))
                .status(InvoiceStatus.PENDING)
                .totalAmountCents(cost)
                .currency(policy.getCurrency())
                .createdAt(Instant.now())
                .build();

        invoiceRepository.save(invoice);

        // Initiate payment
        boolean success = paymentGatewayClient.processPayment(invoice.getInvoiceId(), cost, invoice.getCurrency());
        if (success) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(Instant.now());
            invoiceRepository.save(invoice);

            eventPublisher.publishEvent(new PaymentProcessedEvent(
                    invoice.getInvoiceId(),
                    invoice.getUserId(),
                    invoice.getReservationId(),
                    invoice.getTotalAmountCents(),
                    Instant.now()
            ));
        }
    }

    @ApplicationModuleListener
    public void onSpaceVacated(SpaceVacatedEvent event) {
        if (event.hasChargingPoint() == HasChargingPoint.TRUE) {
            // Logic to append EV_CHARGING cost to existing invoice
            // For simplicity, we look for a PENDING or recent PAID invoice for this user and reservation
            // If none found (e.g., walk-in), we might create a new one.
            // In a real system, we'd need a more robust way to correlate walk-ins.
            // Assuming we correlate by userId and spaceId for currently being processed.
            
            PricingPolicy policy = zoneService.getPricingPolicy(event.zoneId());
            long chargingCost = priceCalculator.calculateChargingCost(10, policy); // Placeholder: 10 kWh

            BillingItem item = BillingItem.builder()
                    .type(BillingItemType.EV_CHARGING)
                    .description("EV Charging fee")
                    .amountCents(chargingCost)
                    .build();

            // Find invoice or create new
            // Simplified: always create a separate one for charging if not easily correlated in this demo
            Invoice invoice = Invoice.builder()
                    .userId(event.userId())
                    .items(new ArrayList<>(List.of(item)))
                    .status(InvoiceStatus.PENDING)
                    .totalAmountCents(chargingCost)
                    .currency(policy.getCurrency())
                    .createdAt(Instant.now())
                    .build();

            invoiceRepository.save(invoice);
        }
    }
}
