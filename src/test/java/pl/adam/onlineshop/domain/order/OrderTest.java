package pl.adam.onlineshop.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderTest {
    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000010"
    );

    private static final UUID LAPTOP_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000011"
    );

    private static final UUID COMPUTER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000012"
    );

    private Customer createCustomer() {
        return new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );
    }

    private OrderItem createOrderItem(UUID productId, String productName, String unitPrice, int quantity) {
        return new OrderItem(
                productId,
                productName,
                new BigDecimal(unitPrice),
                quantity
        );
    }

    @Test
    @DisplayName("Should create order with customer and items")
    void shouldCreateOrderWithCustomerAndItems() {
        // Arrange
        Customer customer = createCustomer();
        OrderItem orderItem = createOrderItem(
                LAPTOP_ID,
                "Gaming Laptop",
                "199.99",
                2
        );

        // Act
        Order order = new Order(
                ORDER_ID,
                customer,
                List.of(orderItem)
        );

        // Assert
        assertThat(order.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(order.getCustomer()).isEqualTo(customer);
        assertThat(order.getItems()).containsExactly(orderItem);
    }

    @Test
    @DisplayName("Should calculate total amount from all items")
    void shouldCalculateTotalAmount() {
        OrderItem laptop = createOrderItem(
                LAPTOP_ID,
                "Gaming Laptop",
                "199.99",
                2
        );

        OrderItem computer = createOrderItem(
                COMPUTER_ID,
                "Gaming Computer",
                "2000.00",
                1
        );

        // Act
        Order order = new Order(
                ORDER_ID,
                createCustomer(),
                List.of(laptop, computer)
        );

        // Assert
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("2399.98"));
    }

    @Test
    @DisplayName("Should reject null order id")
    void shouldRejectNullOrderId() {
        // Arrange
        OrderItem orderItem = createOrderItem(
                COMPUTER_ID,
                "Gaming Laptop",
                "199.99",
                2
        );

        // Act + Assert
        assertThatThrownBy(() -> new Order(
                null,
                createCustomer(),
                List.of(orderItem)
        ))
                .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Order id must not be null");
    }
}
