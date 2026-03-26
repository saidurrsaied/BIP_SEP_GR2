package com.parking.billing;

import java.time.Instant;

public record PaymentProcessedEvent(
        Long invoiceId,
        Long userId,
        String userEmail,
        long totalAmountCents,
        Instant occurredAt
) {}