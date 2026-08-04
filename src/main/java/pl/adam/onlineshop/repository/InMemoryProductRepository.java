package pl.adam.onlineshop.repository;

import lombok.NonNull;
import pl.adam.onlineshop.domain.product.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<UUID, Product> products = new ConcurrentHashMap<>();

    @Override
    public void save(@NonNull Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(@NonNull UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public void deleteById(@NonNull UUID id) {
        products.remove(id);
    }

    @Override
    public boolean existsById(@NonNull UUID id) {
        return products.containsKey(id);
    }
}
