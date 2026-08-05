package pl.adam.onlineshop.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.promotion.Promotion;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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

    private static final Instant ISSUED_AT = Instant.parse("2026-08-02T10:00:00Z");

    private static final Clock CLOCK = Clock.fixed(ISSUED_AT, ZoneOffset.UTC);

    private final InvoiceGenerator invoiceGenerator = new InvoiceGenerator(CLOCK);

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

        // Act
        Invoice invoice = invoiceGenerator.generate(order);

        // Assert
        assertThat(invoice.getInvoiceId()).isNotNull();
        assertThat(invoice.getOrderId()).isEqualTo(order.getOrderId());
        assertThat(invoice.getCustomer()).isEqualTo(order.getCustomer());
        assertThat(invoice.getItems()).containsExactlyElementsOf(order.getItems());
        assertThat(invoice.getSubtotalAmount()).isEqualByComparingTo(order.getSubtotalAmount());
        assertThat(invoice.hasPromotion()).isFalse();
        assertThat(invoice.getDiscountAmount()).isEqualByComparingTo(order.getDiscountAmount());
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo(order.getTotalAmount());
        assertThat(invoice.getIssuedAt()).isEqualTo(ISSUED_AT);
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

    @Test
    @DisplayName("Should copy promotion details to invoice")
    void shouldCopyPromotionDetailsToInvoice() {
        // Arrange
        Order order = createOrder();

        Promotion promotion = new Promotion(
                "SAVE10",
                new BigDecimal("10")
        );

        order.applyPromotion(promotion);
        order.markAsProcessing();
        order.complete();

        // Act
        Invoice invoice = invoiceGenerator.generate(order);

        // Assert
        assertThat(invoice.hasPromotion()).isTrue();
        assertThat(invoice.getPromotion()).isSameAs(promotion);
        assertThat(invoice.getSubtotalAmount()).isEqualByComparingTo(order.getSubtotalAmount());
        assertThat(invoice.getDiscountAmount()).isEqualByComparingTo(order.getDiscountAmount());
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo(order.getTotalAmount());
    }
}
