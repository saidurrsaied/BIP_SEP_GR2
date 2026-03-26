package com.parking.billing.internal;

import com.parking.billing.*;
import com.parking.zonemanagement.ChargingPoint;
import com.parking.zonemanagement.SpaceVacatedEvent;
import com.parking.zonemanagement.PricingPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventListener {

    private final InvoiceRepository invoiceRepository;
    private final PriceCalculator priceCalculator;
    private final PaymentGatewayClient paymentGatewayClient;
    private final ApplicationEventPublisher eventPublisher;

    @ApplicationModuleListener
    public void onSpaceVacated(SpaceVacatedEvent event) {
        // Create invoice for parking and optionally EV charging
        try {
            PricingPolicy policy = event.pricingPolicy();
            long parkingCost = priceCalculator.calculateParkingCost(event.occupationDurationMinutes(), policy);

            BillingItem parkingItem = BillingItem.builder()
                    .type(BillingItemType.PARKING)
                    .description("Parking fee for " + event.occupationDurationMinutes() + " minutes")
                    .amountCents(parkingCost)
                    .durationMinutes((int) event.occupationDurationMinutes())
                    .build();

            List<BillingItem> items = new ArrayList<>(List.of(parkingItem));
            long totalCost = parkingCost;

            // Add EV charging cost if applicable
            if (event.chargingPoint() != null && !event.chargingPoint().equals(ChargingPoint.FALSE)) {
                // For now, assume duration is also in minutes for charging (simplified model)
                // In real implementation, you'd have actual kWh consumed
                long estimatedKwh = event.occupationDurationMinutes() / 60;
                if (estimatedKwh < 1) estimatedKwh = 1; // Minimum 1 kWh

                boolean isFastCharger = event.chargingPoint().equals(ChargingPoint.FAST_CHARGER);
                long chargingCost = priceCalculator.calculateChargingCost(estimatedKwh, policy, isFastCharger);

                BillingItem chargingItem = BillingItem.builder()
                        .type(BillingItemType.EV_CHARGING)
                        .description("EV charging: " + event.chargingPoint().name() + " (" + estimatedKwh + " kWh)")
                        .amountCents(chargingCost)
                        .durationMinutes((int) event.occupationDurationMinutes())
                        .build();
                items.add(chargingItem);
                totalCost += chargingCost;
            }

            Invoice invoice = Invoice.builder()
                    .userId(event.userId())
                    .reservationId(null)
                    .items(items)
                    .status(InvoiceStatus.PENDING)
                    .totalAmountCents(totalCost)
                    .currency("EUR")
                    .createdAt(Instant.now())
                    .build();

            invoice = invoiceRepository.save(invoice);
            log.info("Invoice created: id={}, amount={} EUR", invoice.getInvoiceId(), totalCost / 100.0);

            // Process payment
            boolean paymentSuccess = paymentGatewayClient.processPayment(invoice.getInvoiceId(), totalCost);
            if (paymentSuccess) {
                invoice.setStatus(InvoiceStatus.PAID);
                invoice.setPaidAt(Instant.now());
                invoiceRepository.save(invoice);
                log.info("Payment successful for invoice: {}", invoice.getInvoiceId());

                // Publish event for notification
                eventPublisher.publishEvent(new PaymentProcessedEvent(
                        invoice.getInvoiceId(),
                        invoice.getUserId(),
                        event.userEmail(),
                        invoice.getTotalAmountCents(),
                        Instant.now()
                ));
            } else {
                invoice.setStatus(InvoiceStatus.FAILED);
                invoiceRepository.save(invoice);
                log.error("Payment failed for invoice: {}", invoice.getInvoiceId());
            }

        } catch (Exception e) {
            log.error("Error processing billing for space vacation: {}", e.getMessage(), e);
        }
    }
}
