package com.parking.billing.internal;

import com.parking.billing.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUserId(Long userId);

    @Query("SELECT i FROM Invoice i where i.userId = :userId AND I.status = com.parking.billing.InvoiceStatus.PENDING ORDER BY i.createdAt DESC LIMIT 1")
    Optional<Invoice> findPendingInvoiceByUserId(Long userId);
}
