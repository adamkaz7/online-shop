package pl.adam.onlineshop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryOrderRepositoryTest {
    private InMemoryOrderRepository orderRepository;
    private Order order;

    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000010"
    );

    private static final UUID SECOND_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000020"
    );

    private static final UUID LAPTOP_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000011"
    );

    private static final UUID MISSING_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000999"
    );

    private Customer createCustomer() {
        return new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );
    }

    private Order createOrder(UUID orderId, UUID productId) {
        OrderItem orderItem = new OrderItem(
                productId,
                "Gaming Laptop",
                new BigDecimal("199.99"),
                2
        );

        return new Order(
                orderId,
                createCustomer(),
                List.of(orderItem)
        );
    }

    @BeforeEach
    public void setUp() {
        orderRepository = new InMemoryOrderRepository();
        order = createOrder(ORDER_ID, LAPTOP_ID);
    }

    @Test
    @DisplayName("Should save and find order")
    public void shouldSaveAndFindOrder() {
        // Act
        orderRepository.save(order);
        Optional<Order> result = orderRepository.findById(ORDER_ID);

        // Assert
        assertThat(result)
                .isPresent()
                .contains(order);
    }

    @Test
    @DisplayName("Should return empty when order does not exist")
    public void shouldReturnEmptyWhenOrderDoesNotExist() {
        // Act
        Optional<Order> result = orderRepository.findById(MISSING_ORDER_ID);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return all orders")
    public void shouldReturnAllOrders() {
        // Arrange
        Order secondOrder = createOrder(SECOND_ORDER_ID, LAPTOP_ID);

        orderRepository.save(order);
        orderRepository.save(secondOrder);

        // Act
        List<Order> result = orderRepository.findAll();

        // Assert
        assertThat(result)
                .containsExactlyInAnyOrder(order, secondOrder);
    }

    @Test
    @DisplayName("Should return true when order exists")
    public void shouldReturnTrueWhenOrderExists() {
        // Arrange
        orderRepository.save(order);

        // Act
        boolean result = orderRepository.existsById(ORDER_ID);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when order does not exist")
    public void shouldReturnFalseWhenOrderDoesNotExist() {
        // Act
        boolean result = orderRepository.existsById(MISSING_ORDER_ID);

        // Assert
        assertThat(result).isFalse();
    }
}
