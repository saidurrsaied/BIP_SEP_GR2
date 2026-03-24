package com.parking.billing;

import java.time.Instant;

public record PaymentProcessedEvent(
    Long invoiceId,
    Long userId,
    Long reservationId,
    long totalAmountCents,
    Instant occurredAt
) {}
