package pl.adam.onlineshop.domain.invoice;

import lombok.Getter;
import lombok.NonNull;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.order.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class Invoice {
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
}
