package pl.adam.onlineshop.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SmartphoneTest {
    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    @Test
    @DisplayName("Should create smartphone with provided product data and default configuration")
    void shouldCreateSmartphoneWithProvidedProductDataAndDefaultConfiguration() {
        // Arrange
        UUID id = PRODUCT_ID;
        String name = "Samsung Galaxy S25";
        BigDecimal price = new BigDecimal("3999.99");
        int availableQuantity = 8;
        List<String> availableColors = List.of("Black", "Silver");
        List<Integer> availableBatteryCapacities = List.of(4500, 5000);
        List<String> availableAccessories = List.of("Case", "Charger", "Headphones");

        // Act
        Smartphone smartphone = new Smartphone(
                id,
                name,
                price,
                availableQuantity,
                availableColors,
                availableBatteryCapacities,
                availableAccessories
        );

        // Assert
        assertThat(smartphone.getId()).isEqualTo(id);
        assertThat(smartphone.getName()).isEqualTo(name);
        assertThat(smartphone.getPrice()).isEqualByComparingTo(price);
        assertThat(smartphone.getAvailableQuantity()).isEqualTo(availableQuantity);
        assertThat(smartphone.getAvailableColors()).containsExactlyElementsOf(availableColors);
        assertThat(smartphone.getAvailableBatteryCapacity()).containsExactlyElementsOf(availableBatteryCapacities);
        assertThat(smartphone.getAvailableAccessories()).containsExactlyElementsOf(availableAccessories);
        assertThat(smartphone.getSelectedColor()).isEqualTo("Black");
        assertThat(smartphone.getSelectedBatteryCapacity()).isEqualTo(4500);
        assertThat(smartphone.getSelectedAccessories()).isEmpty();
    }

    @Test
    @DisplayName("Should configure smartphone with selected options")
    void shouldConfigureSmartphoneWithSelectedOptions() {
        // Arrange
        Smartphone smartphone = new Smartphone(
                PRODUCT_ID,
                "Samsung Galaxy S25",
                new BigDecimal("3999.99"),
                8,
                List.of("Black", "Silver"),
                List.of(4500, 5000),
                List.of("Case", "Charger", "Headphones")
        );

        String selectedColor = "Silver";
        int selectedBatteryCapacity = 5000;
        List<String> selectedAccessories = List.of("Case", "Charger");

        // Act
        smartphone.configure(
                selectedColor,
                selectedBatteryCapacity,
                selectedAccessories
        );

        // Assert
        assertThat(smartphone.getSelectedColor()).isEqualTo(selectedColor);
        assertThat(smartphone.getSelectedBatteryCapacity()).isEqualTo(selectedBatteryCapacity);
        assertThat(smartphone.getSelectedAccessories()).containsExactlyElementsOf(selectedAccessories);
    }

    @Test
    @DisplayName("Should throw exception when color is not available")
    void shouldThrowExceptionWhenColorIsNotAvailable() {
        // Arrange
        Smartphone smartphone = new Smartphone(
                PRODUCT_ID,
                "Samsung Galaxy S25",
                new BigDecimal("3999.99"),
                8,
                List.of("Black", "Silver"),
                List.of(4500, 5000),
                List.of("Case", "Charger", "Headphones")
        );

        String unavailableColor = "Blue";

        // Act + Assert
        assertThatThrownBy(() -> smartphone.configure(
                unavailableColor,
                4500,
                List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Color Blue is not available for this smartphone");
    }

    @Test
    @DisplayName("Should throw exception when battery capacity is not available")
    void shouldThrowExceptionWhenBatteryCapacityIsNotAvailable() {
        // Arrange
        Smartphone smartphone = new Smartphone(
                PRODUCT_ID,
                "Samsung Galaxy S25",
                new BigDecimal("3999.99"),
                8,
                List.of("Black", "Silver"),
                List.of(4500, 5000),
                List.of("Case", "Charger", "Headphones")
        );

        int unavailableBatteryCapacity = 6000;

        // Act + Assert
        assertThatThrownBy(() -> smartphone.configure(
                "Black",
                unavailableBatteryCapacity,
                List.of()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Battery capacity 6000 mAh is not available for this smartphone");
    }

    @Test
    @DisplayName("Should throw exception when accessory is not available")
    void shouldThrowExceptionWhenAccessoryIsNotAvailable() {
        // Arrange
        Smartphone smartphone = new Smartphone(
                PRODUCT_ID,
                "Samsung Galaxy S25",
                new BigDecimal("3999.99"),
                8,
                List.of("Black", "Silver"),
                List.of(4500, 5000),
                List.of("Case", "Charger", "Headphones")
        );

        List<String> unavailableAccessories = List.of(
                "Case",
                "Power Bank"
        );

        // Act + Assert
        assertThatThrownBy(() -> smartphone.configure(
                "Black",
                4500,
                unavailableAccessories
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one accessories are not available for this smartphone");
    }

    @Test
    @DisplayName("Should throw exception when color list is empty")
    void shouldThrowExceptionWhenColorListIsEmpty() {
        // Arrange
        List<String> emptyColorList = List.of();

        // Act + Assert
        assertThatThrownBy(() -> new Smartphone(
                PRODUCT_ID,
                "Samsung Galaxy S25",
                new BigDecimal("3999.99"),
                8,
                emptyColorList,
                List.of(4500, 5000),
                List.of("Case", "Charger")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Available colors cannot be empty");

    }

    @Test
    @DisplayName("Should throw exception when battery capacity list is empty")
    void shouldThrowExceptionWhenBatteryCapacityListIsEmpty() {
        // Arrange
        List<Integer> emptyBatteryCapacityList = List.of();

        // Act + Assert
        assertThatThrownBy(() -> new Smartphone(
                PRODUCT_ID,
                "Samsung Galaxy S25",
                new BigDecimal("3999.99"),
                8,
                List.of("Black", "Silver"),
                emptyBatteryCapacityList,
                List.of("Case", "Charger")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Available battery capacity cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when selected accessories are null")
    void shouldThrowExceptionWhenSelectedAccessoriesAreNull() {
        // Arrange
        Smartphone smartphone = new Smartphone(
                PRODUCT_ID,
                "Samsung Galaxy S25",
                new BigDecimal("3999.99"),
                8,
                List.of("Black", "Silver"),
                List.of(4500, 5000),
                List.of("Case", "Charger", "Headphones")
        );

        // Act + Assert
        assertThatThrownBy(() -> smartphone.configure(
                "Black",
                4500,
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Available accessories cannot be null");
    }
}
