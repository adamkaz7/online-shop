package pl.adam.onlineshop.cart;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import pl.adam.onlineshop.product.Product;

@Getter
public class CartItem {
    @NonNull
    private final Product product;
    private int quantity;

    public CartItem(@NonNull Product product, int quantity) {
        validateQuantity(quantity);
        this.product = product;
        this.quantity = quantity;
    }

    public void changeQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public void increaseQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity += quantity;
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    @Override
    public String toString() {
        return "Cart item: " + product + ", quantity: " + quantity;
    }
}
