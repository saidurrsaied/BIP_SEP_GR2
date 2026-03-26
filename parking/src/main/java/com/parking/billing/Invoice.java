package com.parking.billing;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "bil_invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private Long userId; // Reference to user (ID only, no entity)

    private Long reservationId; // Reference to reservation (ID only, no entity)

    private Long totalAmountCents;

    @Builder.Default
    private String currency = "EUR";

    private Instant paidAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.PENDING;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private List<BillingItem> items;

    private Instant createdAt;
}