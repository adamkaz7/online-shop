package pl.adam.onlineshop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryCustomerRepositoryTest {
    private InMemoryCustomerRepository customerRepository;
    private Customer customer;

    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID SECOND_CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    @BeforeEach
    public void setUp() {
        customerRepository = new InMemoryCustomerRepository();
        customer = new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );
    }

    @Test
    @DisplayName("Should save and find customer")
    void shouldSaveAndFindCustomer() {
        // Act
        customerRepository.save(customer);

        // Assert
        Optional<Customer> result = customerRepository.findById(customer.getId());

        assertThat(result)
                .isPresent()
                .contains(customer);
    }

    @Test
    @DisplayName("Should return empty when customer does not exist")
    void shouldReturnEmptyWhenCustomerDoesNotExist() {
        // Act
        Optional<Customer> result = customerRepository.findById(SECOND_CUSTOMER_ID);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return all customers")
    void shouldReturnAllCustomers() {
        // Arrange
        Customer secondCustomer = new Customer(
                SECOND_CUSTOMER_ID,
                "Jan Kowalski"
        );

        customerRepository.save(customer);
        customerRepository.save(secondCustomer);

        // Act
        List<Customer> result = customerRepository.findAll();

        // Assert
        assertThat(result).containsExactlyInAnyOrder(customer, secondCustomer);
    }

    @Test
    @DisplayName("Should return true when customer exists")
    void shouldReturnTrueWhenCustomerExists() {
        // Arrange
        customerRepository.save(customer);

        // Act
        boolean result = customerRepository.existsById(customer.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when customer does not exist")
    void shouldReturnFalseWhenCustomerDoesNotExist() {
        // Act
        boolean result = customerRepository.existsById(customer.getId());

        // Assert
        assertThat(result).isFalse();
    }
}
