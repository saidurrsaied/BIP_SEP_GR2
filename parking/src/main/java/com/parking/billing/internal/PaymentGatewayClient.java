package com.parking.billing.internal;

import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayClient {
    public boolean processPayment(String invoiceId, long amountCents, String currency) {
        // Placeholder for external payment provider API call
        return true;
    }
}
