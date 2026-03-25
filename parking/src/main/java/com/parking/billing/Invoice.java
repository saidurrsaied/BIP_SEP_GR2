package com.parking.billing;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long totalAmountCents;  // always in cents

    private String currency;

    private Instant paidAt;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BillingItem> items;

    private Long reservationId;
    private Instant createdAt;

    // Compatibility methods
    public Long getInvoiceId() {
        return id;
    }

    public Long getTotalAmountCents() {
        return totalAmountCents;
    }

    public void setTotalAmount(long totalAmountCents) {
        this.totalAmountCents = totalAmountCents;
    }
}