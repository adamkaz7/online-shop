package pl.adam.onlineshop.repository;

import lombok.NonNull;
import pl.adam.onlineshop.product.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> products = new HashMap<>();

    @Override
    public void save(@NonNull Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(@NonNull String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public void deleteById(@NonNull String id) {
        products.remove(id);
    }

    @Override
    public boolean existsById(@NonNull String id) {
        return products.containsKey(id);
    }
}
