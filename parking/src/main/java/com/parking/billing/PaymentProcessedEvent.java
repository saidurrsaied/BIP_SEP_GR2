package com.parking.billing;

import java.time.Instant;

public record PaymentProcessedEvent(
        Long invoiceId,
        Long userId,
        long totalAmountCents,
        Instant occurredAt
) {}