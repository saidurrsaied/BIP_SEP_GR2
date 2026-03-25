package com.parking.billing.internal;

import com.parking.billing.*;
import com.parking.reservation.ReservationCompletedEvent;
import com.parking.zonemanagement.ChargingPoint;
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
                .createdAt(Instant.now())
                .build();

        invoiceRepository.save(invoice);

        // Initiate payment
        boolean success = paymentGatewayClient.processPayment(invoice.getInvoiceId(), cost);
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
        if (event.chargingPoint() != ChargingPoint.FALSE) {
            PricingPolicy policy = zoneService.getPricingPolicy(event.zoneId());
            long chargingCost = priceCalculator.calculateChargingCost(10, policy, event.chargingPoint() == ChargingPoint.FAST_CHARGER); // Placeholder: 10 kWh

            BillingItem item = BillingItem.builder()
                    .type(BillingItemType.EV_CHARGING)
                    .description("EV Charging fee")
                    .amountCents(chargingCost)
                    .build();

            Invoice existingInvoice = invoiceRepository.findPendingInvoiceByUserId(event.userId())
                    .orElseThrow(() -> new IllegalStateException("Did not find any active parking invoice for user: " + event.userId()));

            existingInvoice.getItems().add(item);
            existingInvoice.setTotalAmountCents(existingInvoice.getTotalAmountCents() + chargingCost);

            invoiceRepository.save(existingInvoice);
        }
    }
}
