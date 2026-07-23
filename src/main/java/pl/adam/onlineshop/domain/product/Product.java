package pl.adam.onlineshop.domain.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Product {
    @NonNull
    private final UUID id;

    @Setter
    @NonNull
    private String name;

    @Setter
    @NonNull
    private BigDecimal price;

    @Setter
    private int availableQuantity;

    @Override
    public String toString() {
        return String.format(
                "ID: %s | %s | Price: %s zł | Available Quantity: %d",
                id, name, price, availableQuantity
        );
    }
}
