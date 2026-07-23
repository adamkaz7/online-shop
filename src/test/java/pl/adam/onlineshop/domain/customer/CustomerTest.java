package pl.adam.onlineshop.domain.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomerTest {
    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    @Test
    @DisplayName("Should create customer with provided data")
    public void shouldCreateCustomerWithProvidedData() {
        // Arrange
        String fullName = "Adam Kowalski";

        // Act
        Customer customer = new Customer(CUSTOMER_ID, fullName);

        // Assert
        assertThat(customer.getId()).isEqualTo(CUSTOMER_ID);
        assertThat(customer.getFullName()).isEqualTo(fullName);
    }

    @Test
    @DisplayName("Should reject null customer id")
    public void shouldRejectNullCustomerId() {
        // Act + Assert
        assertThatThrownBy(() -> new Customer(
                null,
                "Adam Kowalski"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer id must not be null");
    }

    @Test
    @DisplayName("Should reject blank customer full name")
    public void shouldRejectBlankCustomerFullName() {
        // Act + Assert
        assertThatThrownBy(() -> new Customer(
                CUSTOMER_ID,
                " "
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Customer full name must not be blank");
    }
}
