package pl.adam.onlineshop.domain.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.product.Electronics;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.CartItemNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CartTest {
    private static final UUID FIRST_PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID SECOND_PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    private static final UUID THIRD_PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000003"
    );

    private static final UUID MISSING_PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000999"
    );

    private Product createProduct(UUID id) {
        return new Electronics(
                id,
                "Second test electronics",
                new BigDecimal("50.00"),
                5
        );
    }

    @Test
    @DisplayName("Should create empty cart")
    void shouldCreateEmptyCart() {
        // Arrange + Act
        Cart cart = new Cart();

        // Assert
        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotalQuantity()).isZero();
    }

    @Test
    @DisplayName("Should add new product to cart")
    void shouldAddNewProductToCart() {
        // Arrange
        Cart cart = new Cart();
        Product product = createProduct(FIRST_PRODUCT_ID);

        // Act
        cart.addProduct(product, 2);

        // Assert
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.isEmpty()).isFalse();

        assertThat(cart.getItems())
                .singleElement()
                .satisfies(item -> {
                    assertThat(item.getProduct()).isEqualTo(product);
                    assertThat(item.getQuantity()).isEqualTo(2);
                });
    }

    @Test
    @DisplayName("Should increase quantity when product already exists in cart")
    void shouldIncreaseQuantityWhenProductAlreadyExists() {
        // Arrange
        Cart cart = new Cart();
        Product product = createProduct(FIRST_PRODUCT_ID);
        cart.addProduct(product, 2);

        // Act
        cart.addProduct(product, 3);

        // Assert
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should add different products as separate cart items")
    void shouldAddDifferentProductsAsSeparateCartItems() {
        // Arrange
        Cart cart = new Cart();
        Product firstProduct = createProduct(FIRST_PRODUCT_ID);
        Product secondProduct = createProduct(SECOND_PRODUCT_ID);

        // Act
        cart.addProduct(firstProduct, 1);
        cart.addProduct(secondProduct, 2);

        // Assert
        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getTotalQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should find cart item by product id")
    void shouldFindCartItemById() {
        // Arrange
        Cart cart = new Cart();
        Product product = createProduct(FIRST_PRODUCT_ID);
        cart.addProduct(product, 2);

        // Act
        Optional<CartItem> result = cart.findItemByProductId(product.getId());

        // Assert
        assertThat(result)
                .isPresent()
                .get()
                .satisfies(item -> {
                    assertThat(item.getProduct()).isEqualTo(product);
                    assertThat(item.getQuantity()).isEqualTo(2);
                });
    }

    @Test
    @DisplayName("Should return empty optional when product is not in cart")
    void shouldReturnEmptyOptionalWhenProductIsNotInCart() {
        // Arrange
        Cart cart = new Cart();

        // Act
        Optional<CartItem> result = cart.findItemByProductId(MISSING_PRODUCT_ID);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should remove product from cart")
    void shouldRemoveProductFromCart() {
        // Arrange
        Cart cart = new Cart();
        Product product = createProduct(FIRST_PRODUCT_ID);
        cart.addProduct(product, 2);

        // Act
        cart.removeProduct(product.getId());

        // Assert
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should return total quantity of all products")
    void shouldReturnTotalQuantityOfAllProducts() {
        // Arrange
        Cart cart = new Cart();

        cart.addProduct(createProduct(FIRST_PRODUCT_ID), 2);
        cart.addProduct(createProduct(SECOND_PRODUCT_ID), 3);
        cart.addProduct(createProduct(THIRD_PRODUCT_ID), 4);

        // Act
        int totalQuantity = cart.getTotalQuantity();

        // Assert
        assertThat(totalQuantity).isEqualTo(9);
    }

    @Test
    @DisplayName("Should clear all items from cart")
    void shouldClearAllItemsFromCart() {
        // Arrange
        Cart cart = new Cart();
        cart.addProduct(createProduct(FIRST_PRODUCT_ID), 2);
        cart.addProduct(createProduct(SECOND_PRODUCT_ID), 3);

        // Act
        cart.clearCart();

        // Assert
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.getTotalQuantity()).isZero();
    }

    @Test
    @DisplayName("Should throw exception when removing product not present in cart")
    void shouldThrowExceptionWhenRemovingProductNotPresentInCart() {
        // Arrange
        Cart cart = new Cart();

        // Act + Assert
        assertThatThrownBy(() -> cart.removeProduct(MISSING_PRODUCT_ID))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessage("Product with id: 00000000-0000-0000-0000-000000000999 not found in cart");
    }

    @Test
    @DisplayName("Should throw exception when changing quantity of missing product")
    void shouldThrowExceptionWhenChangingQuantityOfMissingProduct() {
        // Arrange
        Cart cart = new Cart();

        // Act + Assert
        assertThatThrownBy(() -> cart.changeQuantity(MISSING_PRODUCT_ID, 5))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessage("Product with id: 00000000-0000-0000-0000-000000000999 not found in cart");
    }
}
