package pl.adam.onlineshop.domain.product;

import java.math.BigDecimal;
import java.util.UUID;

public class Electronics extends Product {

    public Electronics(UUID id, String name, BigDecimal price, int availableQuantity) {
        super(id, name, price, availableQuantity);
    }
}
