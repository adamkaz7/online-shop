package pl.adam.onlineshop.domain.invoice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvoiceTest {
    private static final UUID INVOICE_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000003"
    );

    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000004"
    );

    private Customer createCustomer() {
        return new Customer(CUSTOMER_ID, "Jan Kowalski");
    }

    private OrderItem createOrderItem() {
        return new OrderItem(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("199.99"),
                1
        );
    }

    @Test
    @DisplayName("Should create invoice with provided data")
    void shouldCreateInvoiceWithProvidedData() {
        // Arrange
        Customer customer = createCustomer();
        OrderItem item = createOrderItem();
        BigDecimal totalAmount = new BigDecimal("199.99");
        LocalDateTime issuedAt = LocalDateTime.of(2026, 7, 29, 10, 0);

        // Act
        Invoice invoice = new Invoice(
                INVOICE_ID,
                ORDER_ID,
                customer,
                List.of(item),
                totalAmount,
                issuedAt
        );

        // Assert
        assertThat(invoice.getInvoiceId()).isEqualTo(INVOICE_ID);
        assertThat(invoice.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(invoice.getCustomer()).isEqualTo(customer);
        assertThat(invoice.getItems()).containsExactly(item);
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo(totalAmount);
        assertThat(invoice.getIssuedAt()).isEqualTo(issuedAt);
    }

    @Test
    @DisplayName("Should protect items from modification")
    void shouldProtectItemsFromModification() {
        // Arrange
        OrderItem item = createOrderItem();
        List<OrderItem> items = new ArrayList<>(List.of(item));

        Invoice invoice = new Invoice(
                INVOICE_ID,
                ORDER_ID,
                createCustomer(),
                items,
                item.calculateSubtotal(),
                LocalDateTime.now()
        );

        // Act
        items.clear();

        // Assert
        assertThat(invoice.getItems()).containsExactly(item);
        assertThatThrownBy(() -> invoice.getItems().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
