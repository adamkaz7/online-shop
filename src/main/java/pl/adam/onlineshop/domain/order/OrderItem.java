package pl.adam.onlineshop.domain.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(UUID productId, String productName, BigDecimal unitPrice, int quantity) {
    public OrderItem {
        validateProductId(productId);
        validateProductName(productName);
        validateUnitPrice(unitPrice);
        validateQuantity(quantity);
    }

    private static void validateProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }
    }

    private static void validateProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
    }

    private static void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price must not be null");
        }

        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Unit price must not be negative");
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Product ID: %s | Unit price: %s zł | Quantity: %d | Subtotal: %s zł",
                productName,
                productId,
                unitPrice,
                quantity,
                calculateSubtotal()
        );
    }
}
