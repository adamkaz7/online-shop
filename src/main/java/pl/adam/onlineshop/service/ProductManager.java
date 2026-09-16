package pl.adam.onlineshop.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.ProductAlreadyExistsException;
import pl.adam.onlineshop.exception.ProductNotFoundException;
import pl.adam.onlineshop.repository.ProductRepository;

import java.util.List;

@RequiredArgsConstructor
public class ProductManager {
    @NonNull
    private final ProductRepository productRepository;

    public void addProduct(@NonNull Product product) {
        if (productRepository.existsById(product.getId())) {
            throw new ProductAlreadyExistsException(product.getId());
        }
        productRepository.save(product);
    }

    public void updateProduct(@NonNull Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new ProductNotFoundException(product.getId());
        }
        productRepository.save(product);
    }

    public void removeProduct(@NonNull String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    public Product findProductById(@NonNull String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
