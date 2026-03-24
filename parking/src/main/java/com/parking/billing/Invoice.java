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
    private String invoiceId;

    private String userId; // Plain ID
    private String reservationId; // Plain ID

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoiceId")
    private List<BillingItem> items;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private long totalAmountCents;
    private String currency;
    private Instant createdAt;
    private Instant paidAt;
}
