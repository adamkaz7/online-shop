package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class InvoiceGenerator {
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
                LocalDateTime.now()
        );
    }
}
