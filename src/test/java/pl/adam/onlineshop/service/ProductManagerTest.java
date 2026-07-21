package pl.adam.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.adam.onlineshop.domain.product.Electronics;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.ProductAlreadyExistsException;
import pl.adam.onlineshop.exception.ProductNotFoundException;
import pl.adam.onlineshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductManagerTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductManager productManager;

    private Product product;
    private String id;

    @BeforeEach
    public void setUp() {
        id = "ELE-001";
        product = new Electronics(
                id,
                "Wireless Headphones",
                new BigDecimal("299.99"),
                10
        );
    }

    @Test
    @DisplayName("Should add product when product ID does not exist")
    public void shouldAddProductWhenProductIDDoesNotExist() {
        // Arrange
        when(productRepository.existsById(id)).thenReturn(false);

        // Act
        productManager.addProduct(product);

        // Assert
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw exception when adding product with existing ID")
    public void shouldThrowExceptionWhenAddingProductWithExistingID() {
        // Arrange
        when(productRepository.existsById(id)).thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> productManager.addProduct(product))
                .isInstanceOf(ProductAlreadyExistsException.class)
                .hasMessage("Product with id: " + id + " already exists");

        verify(productRepository, never()).save(product);
    }

    @Test
    @DisplayName("Should update product when product exists")
    public void shouldUpdateProductWhenProductExists() {
        // Arrange
        when(productRepository.existsById(id)).thenReturn(true);

        // Act
        productManager.updateProduct(product);

        // Assert
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should throw exception when updating product that does not exist")
    public void shouldThrowExceptionWhenUpdatingProductThatDoesNotExist() {
        // Arrange
        when(productRepository.existsById(id)).thenReturn(false);

        // Act
        assertThatThrownBy(() -> productManager.updateProduct(product))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Cannot find product with id: " + id);

        verify(productRepository, never()).save(product);
    }

    @Test
    @DisplayName("Should remove product when product exists")
    public void shouldRemoveProductWhenProductExists() {
        // Arrange
        when(productRepository.existsById(id)).thenReturn(true);

        // Act
        productManager.removeProduct(id);

        // Assert
        verify(productRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when removing product that does not exist")
    public void shouldThrowExceptionWhenRemovingProductThatDoesNotExist() {
        // Arrange
        when(productRepository.existsById(id)).thenReturn(false);

        // Act + Assert
        assertThatThrownBy(() -> productManager.removeProduct(id))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Cannot find product with id: " + id);

        verify(productRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Should return product when product exists")
    public void shouldReturnProductWhenProductExists() {
        // Arrange
        when(productRepository.findById(id))
                .thenReturn(Optional.of(product));

        // Act
        Product result = productManager.findProductById(id);

        // Assert
        assertThat(result).isEqualTo(product);
    }

    @Test
    @DisplayName("Should throw exception when product cannot be found")
    public void shouldThrowExceptionWhenFindingProductThatDoesNotExist() {
        // Arrange
        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> productManager.findProductById(id))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Cannot find product with id: " + id);
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

        List<Product> products = List.of(product, secondProduct);

        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productManager.getAllProducts();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(product, secondProduct);
    }
}
