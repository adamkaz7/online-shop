package pl.adam.onlineshop.domain.customer;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Customer {
    private final UUID id;
    private final String fullName;

    public Customer(UUID id, String fullName) {
        validateId(id);
        validateFullName(fullName);

        this.id = id;
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (ID: %s)",
                fullName,
                id
        );
    }

    private static void validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Customer id must not be null");
        }
    }

    private static void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Customer full name must not be blank");
        }
    }
}
