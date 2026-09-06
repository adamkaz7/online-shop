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
        validateOrderStatus(order);

        return Invoice.builder()
                .invoiceId(UUID.randomUUID())
                .orderId(order.getOrderId())
                .customer(order.getCustomer())
                .items(order.getItems())
                .subtotalAmount(order.getSubtotalAmount())
                .promotion(order.getAppliedPromotion())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .issuedAt(Instant.now(clock))
                .build();
    }

    private static void validateOrderStatus(Order order) {
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order status must be PROCESSING");
        }
    }
}
