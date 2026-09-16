package pl.adam.onlineshop.domain.order;

import lombok.Getter;
import lombok.NonNull;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.promotion.Promotion;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
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

    @NonNull
    private final UUID orderId;
    @NonNull
    private final Customer customer;
    @NonNull
    private final List<OrderItem> items;
    @NonNull
    private final BigDecimal subtotalAmount;
    @NonNull
    private BigDecimal discountAmount;
    @NonNull
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
        if (orderId == null) {
            throw new IllegalArgumentException("Order id must not be null");
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order items must not be empty");
        }

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
        if (status != OrderStatus.NEW) {
            throw new IllegalStateException("Promotion can only be applied to NEW order");
        }

        if (appliedPromotion != null) {
            throw new IllegalStateException("Promotion has already been applied");
        }

        BigDecimal calculatedDiscount = promotion.calculateDiscount(subtotalAmount);

        this.appliedPromotion = promotion;
        this.discountAmount = calculatedDiscount;
        this.totalAmount = subtotalAmount.subtract(calculatedDiscount);
    }

    public boolean hasPromotion() {
        return appliedPromotion != null;
    }

    public void markAsProcessing() {
        this.status = OrderStatus.PROCESSING;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
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
                DATE_TIME_FORMATTER.format(orderDate),
                items.size(),
                subtotalAmount,
                promotionCode,
                discountAmount,
                totalAmount
        );
    }
}
