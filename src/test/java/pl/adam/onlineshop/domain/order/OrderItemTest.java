package pl.adam.onlineshop.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderItemTest {
    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    @Test
    @DisplayName("Should calculate subtotal")
    void shouldCalculateSubtotal() {
        // Arrange
        OrderItem orderItem = new OrderItem(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("99.99"),
                2
        );

        // Act
        BigDecimal result = orderItem.calculateSubtotal();

        // Assert
        assertThat(result).isEqualTo(new BigDecimal("199.98"));
    }

    @Test
    @DisplayName("Should reject non-positive quantity")
    void shouldRejectNonPositiveQuantity() {
        // Act + Assert
        assertThatThrownBy(() -> new OrderItem(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("99.99"),
                0
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be positive");
    }

    @Test
    @DisplayName("Should reject negative unit price")
    void shouldRejectNegativeUnitPrice() {
        // Act + Assert
        assertThatThrownBy(() -> new OrderItem(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("-0.01"),
                2
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unit price must not be negative");
    }
}
