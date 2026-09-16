package pl.adam.onlineshop.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderItemTest {
    @Test
    @DisplayName("Should calculate subtotal")
    void shouldCalculateSubtotal() {
        // Arrange
        OrderItem orderItem = new OrderItem(
                "COM-001",
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
                "COM-001",
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
                "COM-001",
                "Gaming Laptop",
                new BigDecimal("-0.01"),
                2
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unit price must be positive");
    }
}
