package com.parking.billing;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bil_billing_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Enumerated(EnumType.STRING)
    private BillingItemType type;

    private String description;
    private long amountCents;
    private Integer durationMinutes;
}
