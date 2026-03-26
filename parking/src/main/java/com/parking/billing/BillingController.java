package com.parking.billing;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

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