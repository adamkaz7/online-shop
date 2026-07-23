package pl.adam.onlineshop.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Cannot find product with id: " + productId);
    }
}
