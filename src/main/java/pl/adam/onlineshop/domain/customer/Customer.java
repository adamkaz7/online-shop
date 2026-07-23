package pl.adam.onlineshop.domain.customer;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Customer {
    private final UUID id;
    private final String fullName;

    public Customer(UUID id, String fullName) {
        if (id == null) {
            throw new IllegalArgumentException("Customer id must not be null");
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Customer full name must not be blank");
        }

        this.id = id;
        this.fullName = fullName;
    }
}
