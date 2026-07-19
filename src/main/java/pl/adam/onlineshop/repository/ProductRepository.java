package pl.adam.onlineshop.repository;

import pl.adam.onlineshop.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}
