package com.parking.billing;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private long amountCents; // in cents

    @Enumerated(EnumType.STRING)
    private BillingItemType type;

    private int durationMinutes; // optional

    // Compatibility method
    public long getAmount() {
        return amountCents;
    }
}