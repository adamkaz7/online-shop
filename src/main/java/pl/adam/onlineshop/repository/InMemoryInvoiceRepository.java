package pl.adam.onlineshop.repository;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

public class InMemoryInvoiceRepository implements InvoiceRepository {
    private final Map<UUID, Invoice> invoices = new HashMap<>();

    @Override
    public void save(@NonNull Invoice invoice) {
        invoices.put(invoice.getInvoiceId(), invoice);
    }

    @Override
    public Optional<Invoice> findById(@NonNull UUID invoiceId) {
        return Optional.ofNullable(invoices.get(invoiceId));
    }

    @Override
    public Optional<Invoice> findByOrderId(@NonNull UUID orderId) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public List<Invoice> findAll() {
        return List.copyOf(invoices.values());
    }
}
