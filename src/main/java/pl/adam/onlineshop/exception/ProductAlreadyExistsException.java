package pl.adam.onlineshop.exception;

import java.util.UUID;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(UUID productId) {
        super("Product with id: " + productId + " already exists");
    }
}
