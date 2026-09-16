package pl.adam.onlineshop.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ElectronicsTest {
    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    @Test
    @DisplayName("Should create electronics with provided product data")
    void shouldCreateElectronicsWithProvidedProductData() {
        //Arrange
        String name = "Wireless Headphones";
        BigDecimal price = new BigDecimal("299.99");
        int availableQuantity = 10;

        //Act
        Electronics electronics = new Electronics(
                PRODUCT_ID,
                name,
                price,
                availableQuantity
        );

        //Assert
        assertThat(electronics.getId()).isEqualTo(PRODUCT_ID);
        assertThat(electronics.getName()).isEqualTo(name);
        assertThat(electronics.getPrice()).isEqualByComparingTo(price);
        assertThat(electronics.getAvailableQuantity()).isEqualTo(availableQuantity);
    }
}
