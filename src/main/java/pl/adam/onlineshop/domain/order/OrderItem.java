package pl.adam.onlineshop.domain.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(UUID productId, String productName, BigDecimal unitPrice, int quantity) {
    public OrderItem {
        if (productId == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }

        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }

        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price must not be null");
        }

        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Unit price must not be negative");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

    }

    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
