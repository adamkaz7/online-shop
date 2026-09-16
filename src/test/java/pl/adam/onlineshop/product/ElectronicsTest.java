package pl.adam.onlineshop.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ElectronicsTest {

    @Test
    @DisplayName("Should create electronics with provided product data")
    void shouldCreateElectronicsWithProvidedProductData() {
        //Arrange
        String id = "ELE-001";
        String name = "Wireless Headphones";
        BigDecimal price = new BigDecimal("299.99");
        int availableQuantity = 10;

        //Act
        Electronics electronics = new Electronics(
                id,
                name,
                price,
                availableQuantity
        );

        //Assert
        assertThat(electronics.getId()).isEqualTo(id);
        assertThat(electronics.getName()).isEqualTo(name);
        assertThat(electronics.getPrice()).isEqualByComparingTo(price);
        assertThat(electronics.getAvailableQuantity()).isEqualTo(availableQuantity);
    }
}
