package pl.adam.onlineshop.domain.product;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public abstract class Product {
    @NonNull
    private final UUID id;

    @NonNull
    private String name;

    @NonNull
    private BigDecimal price;
    private int availableQuantity;

    protected Product(@NonNull UUID id, @NonNull String name, @NonNull BigDecimal price, int availableQuantity) {
        validateName(name);
        validatePrice(price);
        validateAvailableQuantity(availableQuantity);

        this.id = id;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public void changeName(@NonNull String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void changePrice(@NonNull BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }

    public void changeAvailableQuantity(int newQuantity) {
        validateAvailableQuantity(newQuantity);
        this.availableQuantity = newQuantity;
    }

    private static void validateAvailableQuantity(int availableQuantity) {
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity cannot be negative");
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    private static void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s | %s | Price: %s zł | Available Quantity: %d",
                id, name, price, availableQuantity
        );
    }
}
