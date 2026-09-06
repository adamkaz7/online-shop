package pl.adam.onlineshop.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.ProductAlreadyExistsException;
import pl.adam.onlineshop.exception.ProductNotFoundException;
import pl.adam.onlineshop.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductManager {
    @NonNull
    private final ProductRepository productRepository;

    public void addProduct(@NonNull Product product) {
        validateProductDoesNotExist(product.getId());
        productRepository.save(product);
    }

    public void updateProduct(@NonNull Product product) {
        validateProductExists(product.getId());
        productRepository.save(product);
    }

    public void removeProduct(@NonNull UUID id) {
        validateProductExists(id);
        productRepository.deleteById(id);
    }

    public Product findProductById(@NonNull UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    private void validateProductExists(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
    }

    private void validateProductDoesNotExist(UUID id) {
        if (productRepository.existsById(id)) {
            throw new ProductAlreadyExistsException(id);
        }
    }
}
