package pl.adam.onlineshop.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public abstract class Product {
    private final String id;

    @Setter
    private String name;

    @Setter
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
