package pl.adam.onlineshop.domain.customer;

import lombok.Getter;

@Getter
public class Customer {
    private final String id;
    private final String fullName;

    public Customer(String id, String fullName) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Customer id must not be blank");
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Customer full name must not be blank");
        }

        this.id = id;
        this.fullName = fullName;
    }
}
