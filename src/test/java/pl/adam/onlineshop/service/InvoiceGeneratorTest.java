package pl.adam.onlineshop.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceGeneratorTest {
    private static final UUID ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000003"
    );

    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000004"
    );

    private final InvoiceGenerator invoiceGenerator = new InvoiceGenerator();

    private Order createOrder() {
        Customer customer = new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );

        OrderItem item = new OrderItem(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("199.99"),
                2
        );

        return new Order(
                ORDER_ID,
                customer,
                List.of(item)
        );
    }

    @Test
    @DisplayName("Should generate invoice for completed order")
    void shouldGenerateInvoiceForCompletedOrder() {
        // Arrange
        Order order = createOrder();
        order.markAsProcessing();
        order.complete();
        LocalDateTime beforeGeneration = LocalDateTime.now();

        // Act
        Invoice invoice = invoiceGenerator.generate(order);
        LocalDateTime afterGeneration = LocalDateTime.now();

        // Assert
        assertThat(invoice.getInvoiceId()).isNotNull();
        assertThat(invoice.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(invoice.getCustomer()).isEqualTo(order.getCustomer());
        assertThat(invoice.getItems()).containsExactlyElementsOf(order.getItems());
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo(order.getTotalAmount());
        assertThat(invoice.getIssuedAt()).isBetween(beforeGeneration, afterGeneration);
    }

    @Test
    @DisplayName("Should reject order that is not completed")
    void shouldRejectOrderThatIsNotCompleted() {
        // Arrange
        Order order = createOrder();

        // Act + Assert
        Assertions.assertThatThrownBy(() -> invoiceGenerator.generate(order))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Order status must be COMPLETED");
    }
}
