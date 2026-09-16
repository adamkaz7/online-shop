package pl.adam.onlineshop.repository;

import lombok.NonNull;
import pl.adam.onlineshop.domain.customer.Customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<UUID, Customer> customers = new HashMap<>();

    @Override
    public void save(@NonNull Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public Optional<Customer> findById(@NonNull UUID id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return List.copyOf(customers.values());
    }

    @Override
    public boolean existsById(@NonNull UUID id) {
        return customers.containsKey(id);
    }
}
