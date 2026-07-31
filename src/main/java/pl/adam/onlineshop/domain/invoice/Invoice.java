package pl.adam.onlineshop.domain.invoice;

import lombok.Getter;
import lombok.NonNull;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Invoice {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UUID invoiceId;
    private final UUID orderId;
    private final Customer customer;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final LocalDateTime issuedAt;

    public Invoice(
            @NonNull UUID invoiceId,
            @NonNull UUID orderId,
            @NonNull Customer customer,
            @NonNull List<OrderItem> items,
            @NonNull BigDecimal totalAmount,
            @NonNull LocalDateTime issuedAt
    ) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.customer = customer;
        this.items = List.copyOf(items);
        this.totalAmount = totalAmount;
        this.issuedAt = issuedAt;
    }

    @Override
    public String toString() {
        String formattedItems = items.stream()
                .map(item -> " - " + item)
                .collect(Collectors.joining(System.lineSeparator()));

        return String.format(
                "Invoice ID: %s%n"
                        + "Order ID: %s%n"
                        + "Customer: %s%n"
                        + "Issued at: %s%n"
                        + "Items:%n%s%n"
                        + "Total amount: %s zł",
                invoiceId,
                orderId,
                customer,
                issuedAt.format(DATE_TIME_FORMATTER),
                formattedItems,
                totalAmount
        );
    }
}
