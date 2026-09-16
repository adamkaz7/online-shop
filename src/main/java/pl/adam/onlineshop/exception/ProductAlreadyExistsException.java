package pl.adam.onlineshop.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String productId) {
        super("Product with id: " + productId + " already exists");
    }
}
