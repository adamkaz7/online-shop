package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.repository.ProductRepository;

import java.util.List;

public class ProductManager {
    private final ProductRepository productRepository;

    public ProductManager(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProduct(@NonNull Product product) {
        if (productRepository.existsById(product.getId())) {
            throw new IllegalStateException("Product with id " + product.getId() + " already exists");
        }
        productRepository.save(product);
    }

    public void updateProduct(@NonNull Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalStateException("Cannot find product with id " + product.getId());
        }
        productRepository.save(product);
    }

    public void removeProduct(@NonNull String id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Cannot find product with id " + id);
        }
        productRepository.deleteById(id);
    }

    public Product findProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Cannot find product with id " + id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
