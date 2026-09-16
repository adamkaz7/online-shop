package pl.adam.onlineshop.domain.invoice;

import lombok.Getter;
import lombok.NonNull;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.promotion.Promotion;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Invoice {
    private static final ZoneId SHOP_ZONE = ZoneId.of("Europe/Warsaw");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter
                    .ofPattern("yyyy-MM-dd HH:mm:ss XXX VV")
                    .withZone(SHOP_ZONE);

    private final UUID invoiceId;
    private final UUID orderId;
    private final Customer customer;
    private final List<OrderItem> items;
    private final BigDecimal subtotalAmount;
    private final Promotion promotion;
    private final BigDecimal discountAmount;
    private final BigDecimal totalAmount;
    private final Instant issuedAt;

    public Invoice(
            @NonNull UUID invoiceId,
            @NonNull UUID orderId,
            @NonNull Customer customer,
            @NonNull List<OrderItem> items,
            @NonNull BigDecimal totalAmount,
            @NonNull Instant issuedAt
    ) {
        this(
                invoiceId,
                orderId,
                customer,
                items,
                totalAmount,
                null,
                BigDecimal.ZERO.setScale(2),
                totalAmount,
                issuedAt
        );
    }

    public Invoice(
            @NonNull UUID invoiceId,
            @NonNull UUID orderId,
            @NonNull Customer customer,
            @NonNull List<OrderItem> items,
            @NonNull BigDecimal subtotalAmount,
            Promotion promotion,
            @NonNull BigDecimal discountAmount,
            @NonNull BigDecimal totalAmount,
            @NonNull Instant issuedAt
    ) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.subtotalAmount = subtotalAmount;
        this.promotion = promotion;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.issuedAt = issuedAt;
    }

    public boolean hasPromotion() {
        return promotion != null;
    }

    @Override
    public String toString() {
        String formattedItems = items.stream()
                .map(item -> " - " + item)
                .collect(Collectors.joining(System.lineSeparator()));

        String promotionDescription = hasPromotion()
                ? promotion.getCode()
                  + " ("
                  + promotion.getDiscountPercentage()
                  + "%)" : "none";

        return String.format(
                "Invoice ID: %s%n"
                        + "Order ID: %s%n"
                        + "Customer: %s%n"
                        + "Issued at: %s%n"
                        + "Items:%n%s%n"
                        + "Subtotal amount: %s zł%n"
                        + "Promotion: %s%n"
                        + "Discount: %s zł%n"
                        + "Total amount: %s zł",
                invoiceId,
                orderId,
                customer,
                DATE_TIME_FORMATTER.format(issuedAt),
                formattedItems,
                subtotalAmount,
                promotionDescription,
                discountAmount,
                totalAmount
        );
    }
}
