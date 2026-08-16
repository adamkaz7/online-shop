package pl.adam.onlineshop.repository;

import pl.adam.onlineshop.domain.customer.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Design pattern: Repository
// Separates domain logic from the data storage implementation.
public interface CustomerRepository {
    void save(Customer customer);

    Optional<Customer> findById(UUID customerId);

    List<Customer> findAll();

    boolean existsById(UUID customerId);
}
