package com.parking.billing;

import java.time.Instant;

public record PaymentProcessedEvent(
    String invoiceId,
    String userId,
    String reservationId,
    long totalAmountCents,
    Instant occurredAt
) {}
