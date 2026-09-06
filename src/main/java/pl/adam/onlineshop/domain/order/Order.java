package pl.adam.onlineshop.domain.order;

import lombok.Getter;
import lombok.NonNull;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.promotion.Promotion;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
public class Order {
    private static final ZoneId SHOP_ZONE = ZoneId.of("Europe/Warsaw");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss XXX VV")
                    .withZone(SHOP_ZONE);

    private final UUID orderId;
    private final Customer customer;
    private final List<OrderItem> items;
    private final BigDecimal subtotalAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    private Promotion appliedPromotion;
    private final Instant orderDate;
    private OrderStatus status;

    public Order(
            UUID orderId,
            @NonNull Customer customer,
            @NonNull List<OrderItem> items,
            @NonNull Clock clock
    ) {
        validateOrderId(orderId);
        validateItems(items);

        this.orderId = orderId;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.subtotalAmount = calculateSubtotalAmount();
        this.discountAmount = BigDecimal.ZERO.setScale(2);
        this.totalAmount = this.subtotalAmount;
        this.orderDate = Instant.now(clock);
        this.status = OrderStatus.NEW;
    }

    public Order(
            UUID orderId,
            @NonNull Customer customer,
            @NonNull List<OrderItem> items
    ) {
        this(
                orderId,
                customer,
                items,
                Clock.systemUTC()
        );
    }

    private BigDecimal calculateSubtotalAmount() {
        return items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void applyPromotion(@NonNull Promotion promotion) {
        validatePromotionCanBeApplied();

        BigDecimal calculatedDiscount = promotion.calculateDiscount(subtotalAmount);

        this.appliedPromotion = promotion;
        this.discountAmount = calculatedDiscount;
        this.totalAmount = subtotalAmount.subtract(calculatedDiscount);
    }

    public boolean hasPromotion() {
        return appliedPromotion != null;
    }

    public LocalDateTime getOrderDateTime() {
        return LocalDateTime.ofInstant(orderDate, SHOP_ZONE);
    }

    public void markAsProcessing() {
        validateStatusTransition(OrderStatus.NEW, OrderStatus.PROCESSING);
        this.status = OrderStatus.PROCESSING;
    }

    public void complete() {
        validateStatusTransition(OrderStatus.PROCESSING, OrderStatus.COMPLETED);
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        validateStatusTransition(OrderStatus.PROCESSING, OrderStatus.CANCELLED);
        this.status = OrderStatus.CANCELLED;
    }

    private void validatePromotionCanBeApplied() {
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Promotion can only be applied to NEW order");
        }

        if (appliedPromotion != null) {
            throw new IllegalStateException("Promotion has already been applied");
        }
    }

    private void validateStatusTransition(OrderStatus expectedStatus, OrderStatus newStatus) {
        if (status != expectedStatus) {
            throw new IllegalStateException(
                    "Cannot change order status from "
                            + status
                            + " to "
                            + newStatus
            );
        }
    }

    private static void validateOrderId(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order id must not be null");
        }
    }

    private static void validateItems(List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order items must not be empty");
        }
    }

    @Override
    public String toString() {
        String promotionCode = hasPromotion() ? appliedPromotion.getCode() : "none";

        return String.format(
                "Order ID: %s |" +
                        " Customer: %s |" +
                        " Status: %s |" +
                        " Order date: %s |" +
                        " Items: %d |" +
                        " Subtotal amount: %s zł |" +
                        " Promotion: %s |" +
                        " Discount: %s zł |" +
                        " Total amount: %s zł",
                orderId,
                customer,
                status,
                DATE_TIME_FORMATTER.format(
                        getOrderDateTime().atZone(SHOP_ZONE)
                ),
                items.size(),
                subtotalAmount,
                promotionCode,
                discountAmount,
                totalAmount
        );
    }
}
