package pl.adam.onlineshop.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderTest {
    private Customer createCustomer() {
        return new Customer(
                "1",
                "Jan Kowalski"
        );
    }

    private OrderItem createOrderItem(String productId, String productName, String unitPrice, int quantity) {
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
        String orderId = "ORD-1";
        Customer customer = createCustomer();
        OrderItem orderItem = createOrderItem(
                "COM-001",
                "Gaming Laptop",
                "199.99",
                2
        );

        // Act
        Order order = new Order(
                orderId,
                customer,
                List.of(orderItem)
        );

        // Assert
        assertThat(order.getOrderId()).isEqualTo(orderId);
        assertThat(order.getCustomer()).isEqualTo(customer);
        assertThat(order.getItems()).containsExactly(orderItem);
    }

    @Test
    @DisplayName("Should calculate total amount from all items")
    void shouldCalculateTotalAmount() {
        OrderItem laptop = createOrderItem(
                "COM-001",
                "Gaming Laptop",
                "199.99",
                2
        );

        OrderItem computer = createOrderItem(
                "COM-002",
                "Gaming Computer",
                "2000.00",
                1
        );

        // Act
        Order order = new Order(
                "ORD-001",
                createCustomer(),
                List.of(laptop, computer)
        );

        // Assert
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("2399.98"));
    }

    @Test
    @DisplayName("Should reject blank order id")
    void shouldRejectBlankOrderId() {
        // Arrange
        OrderItem orderItem = createOrderItem(
                "COM-001",
                "Gaming Laptop",
                "199.99",
                2
        );

        // Act + Assert
        assertThatThrownBy(() -> new Order(
                " ",
                createCustomer(),
                List.of(orderItem)
        ))
                .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Order id must not be blank");
    }
}
