package pl.adam.onlineshop.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProductTest {
    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-0000000001");

    private Product createProduct() {
        return new Electronics(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("199.99"),
                5
        );
    }

    @Test
    @DisplayName("Should change product name")
    void shouldChangeProductName() {
        // Arrange
        Product product = createProduct();

        // Act
        product.changeName("Laptop");

        // Assert
        assertThat(product.getName()).isEqualTo("Laptop");
    }

    @Test
    @DisplayName("Should change product price")
    void shouldChangeProductPrice() {
        // Arrange
        Product product = createProduct();
        BigDecimal newPrice = new BigDecimal("109.99");

        // Act
        product.changePrice(newPrice);

        // Assert
        assertThat(product.getPrice()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("Should change available quantity")
    void shouldChangeAvailableQuantity() {
        // Arrange
        Product product = createProduct();

        // Act
        product.changeAvailableQuantity(10);

        // Assert
        assertThat(product.getAvailableQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should reject blank name")
    void shouldRejectBlankName() {
        // Arrange
        Product product = createProduct();

        // Act + Assert
        assertThatThrownBy(() -> product.changeName(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be blank");

        assertThat(product.getName()).isEqualTo("Gaming Laptop");
    }

    @Test
    @DisplayName("Should reject negative price")
    void shouldRejectNegativePrice() {
        // Arrange
        Product product = createProduct();
        BigDecimal negativePrice = new BigDecimal("-10.99");

        // Act + Assert
        assertThatThrownBy(() -> product.changePrice(negativePrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price cannot be negative");

        assertThat(product.getPrice()).isEqualByComparingTo("199.99");
    }

    @Test
    @DisplayName("Should reject negative available quantity")
    void shouldRejectNegativeAvailableQuantity() {
        // Arrange
        Product product = createProduct();

        // Act + Assert
        assertThatThrownBy(() -> product.changeAvailableQuantity(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Available quantity cannot be negative");

        assertThat(product.getAvailableQuantity()).isEqualTo(5);
    }
}
