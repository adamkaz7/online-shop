package pl.adam.onlineshop.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.promotion.PercentageDiscountPolicy;
import pl.adam.onlineshop.domain.promotion.Promotion;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
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

    private Order createOrder() {
        return createOrder(Clock.systemUTC());
    }

    private Order createOrder(Clock clock) {
        return new Order(
                ORDER_ID,
                createCustomer(),
                List.of(createOrderItem(
                        LAPTOP_ID,
                        "Gaming Laptop",
                        "199.99",
                        2
                )),
                clock
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
        assertThat(order.getSubtotalAmount()).isEqualByComparingTo(new BigDecimal("2399.98"));
        assertThat(order.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("2399.98"));
        assertThat(order.hasPromotion()).isFalse();
        assertThat(order.getAppliedPromotion()).isNull();
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

    @Test
    @DisplayName("Should reject empty items")
    void shouldRejectEmptyItems() {
        // Act + Assert
        assertThatThrownBy(() -> new Order(
                ORDER_ID,
                createCustomer(),
                List.of()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order items must not be empty");
    }

    @Test
    @DisplayName("Should protect items from modification")
    void shouldProtectItemsFromModification() {
        // Arrange
        OrderItem orderItem = createOrderItem(
                LAPTOP_ID,
                "Gaming Laptop",
                "199.99",
                2
        );

        List<OrderItem> orderItems = new ArrayList<>(List.of(orderItem));
        Order order = new Order(
                ORDER_ID,
                createCustomer(),
                orderItems
        );

        // Act
        orderItems.clear();

        // Assert
        assertThat(order.getItems()).containsExactly(orderItem);
        assertThatThrownBy(() -> order.getItems().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should create order with NEW status and order date")
    void shouldCreateOrderWithNewStatusAndOrderDate() {
        // Arrange
        Instant instant = Instant.parse("2026-08-02T10:00:00Z");
        Clock clock = Clock.fixed(instant, ZoneOffset.UTC);

        // Act
        Order order = createOrder(clock);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
        assertThat(order.getOrderDate()).isEqualTo(instant);
    }

    @Test
    @DisplayName("Should mark order as processing")
    void shouldMarkOrderAsProcessing() {
        // Arrange
        Order order = createOrder();

        // Act
        order.markAsProcessing();

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should complete processing order")
    void shouldCompleteProcessingOrder() {
        // Arrange
        Order order = createOrder();
        order.markAsProcessing();

        // Act
        order.complete();

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should cancel processing order")
    void shouldCancelProcessingOrder() {
        // Arrange
        Order order = createOrder();
        order.markAsProcessing();

        // Act
        order.cancel();

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should reject completing NEW order")
    void shouldRejectCompletingNewOrder() {
        // Arrange
        Order order = createOrder();

        // Act + Assert
        assertThatThrownBy(order::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot change order status from NEW to COMPLETED");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    @DisplayName("Should reject cancelling NEW order")
    void shouldRejectCancellingNewOrder() {
        // Arrange
        Order order = createOrder();

        // Act + Assert
        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot change order status from NEW to CANCELLED");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    @DisplayName("Should reject marking PROCESSING order as processing again")
    void shouldRejectMarkingProcessingOrderAsProcessing() {
        // Arrange
        Order order = createOrder();
        order.markAsProcessing();

        // Act + Assert
        assertThatThrownBy(order::markAsProcessing)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot change order status from PROCESSING to PROCESSING");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should apply promotion to order")
    void shouldApplyPromotion() {
        // Arrange
        Order order = createOrder();

        Promotion promotion = new Promotion(
                "SAVE10",
                new PercentageDiscountPolicy(new BigDecimal("10"))
        );

        // Act
        order.applyPromotion(promotion);

        // Assert
        assertThat(order.hasPromotion()).isTrue();
        assertThat(order.getAppliedPromotion()).isSameAs(promotion);
        assertThat(order.getSubtotalAmount()).isEqualByComparingTo(new BigDecimal("399.98"));
        assertThat(order.getDiscountAmount()).isEqualByComparingTo(new BigDecimal("40.00"));
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("359.98"));
    }

    @Test
    @DisplayName("Should reject second promotion")
    void shouldRejectSecondPromotion() {
        // Arrange
        Order order = createOrder();

        Promotion firstPromotion = new Promotion(
                "SAVE10",
                new PercentageDiscountPolicy(new BigDecimal("10"))
        );

        Promotion secondPromotion = new Promotion(
                "SAVE20",
                new PercentageDiscountPolicy(new BigDecimal("20"))
        );

        order.applyPromotion(firstPromotion);

        // Act + Assert
        assertThatThrownBy(() -> order.applyPromotion(secondPromotion))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Promotion has already been applied");

        assertThat(order.getAppliedPromotion()).isSameAs(firstPromotion);
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("359.98"));
    }

    @Test
    @DisplayName("Should reject promotion when order is not new")
    void shouldRejectPromotionWhenOrderIsNotNew() {
        // Arrange
        Order order = createOrder();

        Promotion promotion = new Promotion(
                "SAVE10",
                new PercentageDiscountPolicy(new BigDecimal("10"))
        );

        order.markAsProcessing();

        // Act + Assert
        assertThatThrownBy(() -> order.applyPromotion(promotion))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Promotion can only be applied to NEW order");

        assertThat(order.hasPromotion()).isFalse();
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("399.98"));
    }
}
