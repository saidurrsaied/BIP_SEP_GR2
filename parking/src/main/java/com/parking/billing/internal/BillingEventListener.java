package com.parking.billing.internal;

import com.parking.billing.*;
import com.parking.zonemanagement.SpaceVacatedEvent;
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
    private final ApplicationEventPublisher eventPublisher;

    @ApplicationModuleListener
    public void onReservationCompleted(SpaceVacatedEvent event) {
        PricingPolicy policy = event.pricingPolicy();
        long cost = priceCalculator.calculateParkingCost(event.occupationDurationMinutes(), policy);

        BillingItem item = BillingItem.builder()
                .type(BillingItemType.PARKING)
                .description("Parking fee for " + event.occupationDurationMinutes() + " minutes")
                .amountCents(cost)
                .durationMinutes((int) event.occupationDurationMinutes())
                .build();

        Invoice invoice = Invoice.builder()
                .userId(event.userId())
                .items(new ArrayList<>(List.of(item)))
                .status(InvoiceStatus.PENDING)
                .totalAmountCents(cost)
                .createdAt(Instant.now())
                .build();

        invoiceRepository.save(invoice);

        boolean success = paymentGatewayClient.processPayment(invoice.getInvoiceId(), cost);
        if (success) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(Instant.now());
            invoiceRepository.save(invoice);

            eventPublisher.publishEvent(new PaymentProcessedEvent(
                    invoice.getInvoiceId(),
                    invoice.getUserId(),
                    invoice.getTotalAmountCents(),
                    Instant.now()
            ));
        }
    }
}
