package com.parking.billing;

import com.parking.billing.internal.InvoiceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;

    public Invoice createInvoice(Invoice invoice) {

        // Sum amounts in cents
        long total = invoice.getItems()
                .stream()
                .mapToLong(BillingItem::getAmount) // ✅ long arithmetic
                .sum();

        invoice.setTotalAmountCents(total);
        invoice.setStatus(InvoiceStatus.CREATED);

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    // Handles PaymentProcessedEvent
    public Invoice processPayment(PaymentProcessedEvent event) {

        Invoice invoice = invoiceRepository.findById(event.invoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setTotalAmountCents(event.totalAmountCents()); // ✅ long
        invoice.setPaidAt(event.occurredAt());
        invoice.setStatus(InvoiceStatus.PAID);

        return invoiceRepository.save(invoice);
    }
}