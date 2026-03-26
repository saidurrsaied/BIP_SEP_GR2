package com.parking.billing.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentGatewayClient {

    @Value("${external-services.payment:http://payment-service/api}")
    private String paymentServiceUrl;

    @Value("${app.payment.mock-mode:true}")
    private boolean mockMode;

    /**
     * Process payment through external payment provider.
     * In production, this would call Stripe, PayPal, or your payment provider's API.
     *
     * @param invoiceId Invoice ID from billing system
     * @param amountCents Amount to charge in cents (e.g., 1000 = €10.00)
     * @return true if payment was successful, false otherwise
     */
    public boolean processPayment(Long invoiceId, long amountCents) {
        try {
            if (mockMode) {
                log.info("MOCK MODE: Processing payment for invoiceId={}, amountCents={}", invoiceId, amountCents);
                // In production, replace this with actual payment processor call:
                // return callStripeAPI(invoiceId, amountCents);
                // return callPayPalAPI(invoiceId, amountCents);
                return true;
            }

            log.info("Processing payment via: {}", paymentServiceUrl);
            log.error("Payment processing not yet implemented for production. Set app.payment.mock-mode=false after integration.");
            return false;

        } catch (Exception e) {
            log.error("Payment processing failed for invoiceId={}: {}", invoiceId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * TODO: Replace with actual payment processor implementation
     * Example for Stripe integration:
     *
     * private boolean callStripeAPI(Long invoiceId, long amountCents) {
     *     try {
     *         Charge charge = Charge.create(
     *             new ChargeCreateParams.Builder()
     *                 .setAmount(amountCents)
     *                 .setCurrency("eur")
     *                 .setSource("tok_visa")
     *                 .setDescription("Invoice #" + invoiceId)
     *                 .build()
     *         );
     *         log.info("Stripe charge created: {}", charge.getId());
     *         return charge.getPaid();
     *     } catch (StripeException e) {
     *         log.error("Stripe payment failed: {}", e.getMessage(), e);
     *         return false;
     *     }
     * }
     */
}
