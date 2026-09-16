package pl.adam.onlineshop.domain.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.product.Electronics;
import pl.adam.onlineshop.domain.product.Product;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CartItemTest {
    private Product createProduct() {
        return new Electronics(
                "1",
                "Test electronics",
                new BigDecimal("100.00"),
                10
        );
    }

    @Test
    @DisplayName("Should create cart item with given product and quantity")
    void shouldCreateCartItemWithGivenProductAndQuantity() {
        // Arrange
        Product product = createProduct();

        // Act
        CartItem cartItem = new CartItem(product, 2);

        // Assert
        assertThat(cartItem.getProduct()).isEqualTo(product);
        assertThat(cartItem.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw exception when quantity is zero")
    void shouldThrowExceptionWhenQuantityIsZero() {
        // Arrange
        Product product = createProduct();

        // Act + Assert
        assertThatThrownBy(() -> new CartItem(product, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be greater than zero.");
    }

    @Test
    @DisplayName("Should throw exception when quantity is negative")
    void shouldThrowExceptionWhenQuantityIsNegative() {
        // Arrange
        Product product = createProduct();

        // Act + Assert
        assertThatThrownBy(() -> new CartItem(product, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be greater than zero.");
    }

    @Test
    @DisplayName("Should change quantity")
    void shouldChangeQuantity() {
        // Arrange
        CartItem cartItem = new CartItem(createProduct(), 2);

        // Act
        cartItem.changeQuantity(5);

        // Assert
        assertThat(cartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should increase quantity")
    void shouldIncreaseQuantity() {
        // Arrange
        CartItem cartItem = new CartItem(createProduct(), 2);

        // Act
        cartItem.increaseQuantity(3);

        // Assert
        assertThat(cartItem.getQuantity()).isEqualTo(5);
    }
}
