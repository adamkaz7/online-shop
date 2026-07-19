package pl.adam.onlineshop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.product.Electronics;
import pl.adam.onlineshop.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryProductRepositoryTest {
    private InMemoryProductRepository productRepository;
    private Product product;

    @BeforeEach
    public void setUp() {
        productRepository = new InMemoryProductRepository();
        product = new Electronics(
                "ELE-001",
                "Wireless Headphones",
                new BigDecimal("299.99"),
                10
        );
    }

    @Test
    @DisplayName("Should save product")
    public void shouldSaveProduct() {
        // Act
        productRepository.save(product);

        // Assert
        Optional<Product> result = productRepository.findById(product.getId());

        assertThat(result)
                .isPresent()
                .contains(product);
    }

    @Test
    @DisplayName("Should find product by id")
    public void shouldFindProductById() {
        // Arrange
        productRepository.save(product);

        // Act
        Optional<Product> result = productRepository.findById(product.getId());

        // Assert
        assertThat(result)
                .isPresent()
                .contains(product);
    }

    @Test
    @DisplayName("Should return all products")
    public void shouldReturnAllProducts() {
        // Arrange
        Product secondProduct = new Electronics(
                "ELE-002",
                "Monitor",
                new BigDecimal("299.99"),
                5
        );

        productRepository.save(product);
        productRepository.save(secondProduct);

        // Act
        List<Product> result = productRepository.findAll();

        // Assert
        assertThat(result)
                .hasSize(2)
                .contains(product, secondProduct);
    }

    @Test
    @DisplayName("Should delete product by id")
    public void shouldDeleteProductById() {
        // Arrange
        productRepository.save(product);

        // Act
        productRepository.deleteById(product.getId());

        // Assert
        assertThat(productRepository.findById(product.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should return true when product exists")
    public void shouldReturnTrueIfProductExists() {
        // Arrange
        productRepository.save(product);

        // Act
        boolean result = productRepository.existsById(product.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when product does not exist")
    public void shouldReturnFalseIfProductDoesNotExist() {
        // Act
        boolean result = productRepository.existsById(product.getId());

        // Assert
        assertThat(result).isFalse();
    }
}
