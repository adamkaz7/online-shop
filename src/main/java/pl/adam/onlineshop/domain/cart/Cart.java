package pl.adam.onlineshop.domain.cart;

import lombok.NonNull;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.CartItemNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public Optional<CartItem> findItemByProductId(@NonNull UUID productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    public void addProduct(@NonNull Product product, int quantity) {
        Optional<CartItem> existingItem = findItemByProductId(product.getId());
        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
            return;
        }
        CartItem item = new CartItem(product, quantity);
        items.add(item);
    }

    public void removeProduct(@NonNull UUID productId) {
        CartItem item = findItemByProductId(productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));

        items.remove(item);
    }

    public void changeQuantity(@NonNull UUID productId, int quantity) {
        CartItem item = findItemByProductId(productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));
        item.changeQuantity(quantity);
    }

    public int getTotalQuantity() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clearCart() {
        items.clear();
    }
}
