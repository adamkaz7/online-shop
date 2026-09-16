package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderStatus;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class InvoiceGenerator {
    private final Clock clock;

    public InvoiceGenerator(@NonNull Clock clock) {
        this.clock = clock;
    }

    public InvoiceGenerator() {
        this(Clock.systemUTC());
    }

    public Invoice generate(@NonNull Order order) {
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order status must be COMPLETED");
        }

        return new Invoice(
                UUID.randomUUID(),
                order.getOrderId(),
                order.getCustomer(),
                order.getItems(),
                order.getSubtotalAmount(),
                order.getAppliedPromotion(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                Instant.now(clock)
        );
    }
}
