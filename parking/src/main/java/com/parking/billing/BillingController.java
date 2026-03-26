package com.parking.billing;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/invoice")
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        return billingService.createInvoice(invoice);
    }

    @GetMapping("/invoice")
    public List<Invoice> getAllInvoices() {
        return billingService.getAllInvoices();
    }

    @GetMapping("/invoice/{id}")
    public Invoice getInvoice(@PathVariable Long id) {
        return billingService.getInvoice(id);
    }

    @PostMapping("/payment")
    public Invoice paymentProcessed(@RequestBody PaymentProcessedEvent event) {
        return billingService.processPayment(event);
    }
}