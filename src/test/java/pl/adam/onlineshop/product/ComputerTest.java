package pl.adam.onlineshop.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComputerTest {
    @Test
    @DisplayName("Should create computer with provided product data and default configuration")
    void shouldCreateComputerWithProvidedProductDataAndDefaultConfiguration() {
        // Arrange
        String id = "COM-001";
        String name = "Gaming Laptop";
        BigDecimal price = new BigDecimal("1999.99");
        int availableQuantity = 5;
        List<String> availableProcessors = List.of("Intel i5", "Intel i7");
        List<Integer> availableRam = List.of(16, 32);

        // Act
        Computer computer = new Computer(
                id,
                name,
                price,
                availableQuantity,
                availableProcessors,
                availableRam);

        // Assert
        assertThat(computer.getId()).isEqualTo(id);
        assertThat(computer.getName()).isEqualTo(name);
        assertThat(computer.getPrice()).isEqualByComparingTo(price);
        assertThat(computer.getAvailableQuantity()).isEqualTo(availableQuantity);
        assertThat(computer.getAvailableProcessors()).containsExactlyElementsOf(availableProcessors);
        assertThat(computer.getAvailableRam()).containsExactlyElementsOf(availableRam);
        assertThat(computer.getSelectedProcessor()).isEqualTo("Intel i5");
        assertThat(computer.getSelectedRam()).isEqualTo(16);
    }

    @Test
    @DisplayName("Should configure computer with selected processor and ram")
    void shouldConfigureComputerWithSelectedProcessorAndRam() {
        // Arrange
        Computer computer = new Computer(
                "COM-001",
                "Gaming Laptop",
                new BigDecimal("1999.99"),
                5,
                List.of("Intel i5", "Intel i7"),
                List.of(16, 32)

        );

        // Act
        computer.configure("Intel i7", 32);

        // Assert
        assertThat(computer.getSelectedProcessor()).isEqualTo("Intel i7");
        assertThat(computer.getSelectedRam()).isEqualTo(32);
    }

    @Test
    @DisplayName("Should throw exception when processor is not available")
    void shouldThrowExceptionWhenProcessorIsNotAvailable() {
        // Arrange
        Computer computer = new Computer(
                "COM-001",
                "Gaming Laptop",
                new BigDecimal("1999.99"),
                5,
                List.of("Intel i5", "Intel i7"),
                List.of(16, 32)
        );

        // Act + Assert
        assertThatThrownBy(() -> computer.configure("Intel i9", 16))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Processor Intel i9 is not available for this computer");
    }

    @Test
    @DisplayName("Should throw exception when ram option is not available")
    void shouldThrowExceptionWhenRamOptionIsNotAvailable() {
        // Arrange
        Computer computer = new Computer(
                "COM-001",
                "Gaming Laptop",
                new BigDecimal("1999.99"),
                5,
                List.of("Intel i5", "Intel i7"),
                List.of(16, 32)
        );

        // Act + Assert
        assertThatThrownBy(() -> computer.configure("Intel i5", 64))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RAM option 64 GB is not available for this computer");
    }

    @Test
    @DisplayName("Should throw exception when processor list is empty")
    void shouldThrowExceptionWhenProcessorListIsEmpty() {
        // Arrange
        String id = "COM-001";
        String name = "Gaming Laptop";
        BigDecimal price = new BigDecimal("1999.99");
        int availableQuantity = 5;
        List<String> availableProcessors = List.of();
        List<Integer> availableRam = List.of(16, 32);

        // Act + Assert
        assertThatThrownBy(() -> new Computer(
                id,
                name,
                price,
                availableQuantity,
                availableProcessors,
                availableRam
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Available processors cannot be empty");
    }
}
