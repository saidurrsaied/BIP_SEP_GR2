package com.parking.billing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getInvoice(id));
    }

    @GetMapping("/users/{userId}/invoices")
    public ResponseEntity<List<Invoice>> getUserInvoices(@PathVariable Long userId) {
        return ResponseEntity.ok(billingService.getUserBillingHistory(userId));
    }
}
