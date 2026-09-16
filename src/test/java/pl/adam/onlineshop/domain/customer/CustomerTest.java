package pl.adam.onlineshop.domain.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomerTest {
    @Test
    @DisplayName("Should create customer with provided data")
    public void shouldCreateCustomerWithProvidedData() {
        // Arrange
        String id = "1";
        String fullName = "Adam Kowalski";

        // Act
        Customer customer = new Customer(id, fullName);

        // Assert
        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getFullName()).isEqualTo(fullName);
    }

    @Test
    @DisplayName("Should reject blank customer id")
    public void shouldRejectBlankCustomerId() {
        // Act + Assert
        assertThatThrownBy(() -> new Customer(
                " ",
                "Adam Kowalski"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer id must not be blank");
    }

    @Test
    @DisplayName("Should reject blank customer full name")
    public void shouldRejectBlankCustomerFullName() {
        // Act + Assert
        assertThatThrownBy(() -> new Customer(
                "1",
                " "
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer full name must not be blank");
    }
}
