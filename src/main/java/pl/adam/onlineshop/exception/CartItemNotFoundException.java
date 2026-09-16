package pl.adam.onlineshop.exception;

import java.util.UUID;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(UUID productId) {
        super("Product with id: " + productId + " not found in cart");
    }
}
