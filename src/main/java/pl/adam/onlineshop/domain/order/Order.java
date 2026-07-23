package pl.adam.onlineshop.domain.order;

import lombok.Getter;
import lombok.NonNull;
import pl.adam.onlineshop.domain.customer.Customer;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class Order {
    @NonNull
    private final String orderId;
    @NonNull
    private final Customer customer;
    @NonNull
    private final List<OrderItem> items;
    @NonNull
    private final BigDecimal totalAmount;

    public Order(@NonNull String orderId, @NonNull Customer customer, @NonNull List<OrderItem> items) {
        if (orderId.isBlank()) {
            throw new IllegalArgumentException("Order id must not be blank");
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order items must not be empty");
        }

        this.orderId = orderId;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.totalAmount = this.calculateTotalAmount();
    }

    private BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return String.format(
                "Order ID: %s | Items: %d | Total amount: %s zł",
                orderId,
                items.size(),
                totalAmount
        );
    }
}
