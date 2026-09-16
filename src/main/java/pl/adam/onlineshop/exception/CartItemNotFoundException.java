package pl.adam.onlineshop.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) {
        super("Product with id: " + message + " not found in cart");
    }
}
