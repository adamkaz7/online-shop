package pl.adam.onlineshop.repository;

import pl.adam.onlineshop.domain.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    void save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAll();

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
