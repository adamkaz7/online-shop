package pl.adam.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.order.OrderStatus;
import pl.adam.onlineshop.domain.product.Electronics;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.InsufficientStockException;
import pl.adam.onlineshop.exception.ProductNotFoundException;
import pl.adam.onlineshop.repository.InvoiceRepository;
import pl.adam.onlineshop.repository.OrderRepository;
import pl.adam.onlineshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderProcessorTest {
    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    private static final UUID FIRST_PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000003"
    );

    private static final UUID SECOND_PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000004"
    );

    private static final UUID MISSING_PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000999"
    );

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    private OrderProcessor orderProcessor;

    @BeforeEach
    void setUp() {
        orderProcessor = new OrderProcessor(
                productRepository,
                orderRepository,
                invoiceRepository,
                new InvoiceGenerator()
        );
    }

    @Test
    @DisplayName("Should process order successfully")
    public void shouldProcessOrderSuccessfully() {
        // Arrange
        Product laptop = createProduct(
                FIRST_PRODUCT_ID,
                "Gaming Laptop",
                "299.99",
                5
        );

        Product headphones = createProduct(
                SECOND_PRODUCT_ID,
                "Headphones",
                "199.99",
                10
        );

        Order order = createOrder(List.of(
                createOrderItem(laptop, 1),
                createOrderItem(headphones, 2)
        ));

        when(productRepository.findById(FIRST_PRODUCT_ID))
                .thenReturn(Optional.of(laptop));

        when(productRepository.findById(SECOND_PRODUCT_ID))
                .thenReturn(Optional.of(headphones));

        // Act
        Invoice invoice = orderProcessor.process(order);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(List.of(laptop, headphones))
                .extracting(Product::getAvailableQuantity)
                .containsExactly(4, 8);
        assertThat(invoice.getOrderId()).isEqualTo(ORDER_ID);

        verify(productRepository, times(2))
                .save(any(Product.class));
        verify(orderRepository).save(order);
        verify(invoiceRepository).save(invoice);
    }

    @Test
    @DisplayName("Should cancel order when product does not exist")
    public void shouldCancelOrderWhenProductDoesNotExist() {
        // Arrange
        Product existingProduct = createProduct(
                FIRST_PRODUCT_ID,
                "Gaming Laptop",
                "299.99",
                5
        );

        OrderItem missingProduct = new OrderItem(
                MISSING_PRODUCT_ID,
                "Missing",
                new BigDecimal("99.99"),
                1
        );

        Order order = createOrder(List.of(
                createOrderItem(existingProduct, 2),
                missingProduct
        ));

        when(productRepository.findById(FIRST_PRODUCT_ID))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository.findById(MISSING_PRODUCT_ID))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> orderProcessor.process(order))
                .isInstanceOf(ProductNotFoundException.class);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(existingProduct.getAvailableQuantity()).isEqualTo(5);

        verify(productRepository, never())
                .save(any(Product.class));
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should not decrease any stock when one product is unavailable")
    public void shouldNotDecreaseAnyStockWhenOneProductIsUnavailable() {
        // Arrange
        Product available = createProduct(
                FIRST_PRODUCT_ID,
                "Gaming Laptop",
                "299.99",
                10
        );

        Product unavailable = createProduct(
                SECOND_PRODUCT_ID,
                "Headphones",
                "99.99",
                1
        );

        Order order = createOrder(List.of(
                createOrderItem(available, 2),
                createOrderItem(unavailable, 2)
        ));

        when(productRepository.findById(FIRST_PRODUCT_ID))
                .thenReturn(Optional.of(available));
        when(productRepository.findById(SECOND_PRODUCT_ID))
                .thenReturn(Optional.of(unavailable));

        // Act + Assert
        assertThatThrownBy(() -> orderProcessor.process(order))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        assertThat(List.of(available, unavailable))
                .extracting(Product::getAvailableQuantity)
                .containsExactly(10, 1);

        verify(productRepository, never())
                .save(any(Product.class));
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should restore stock when invoice generation fails")
    public void shouldRestoreStockWhenInvoiceGenerationFails() {
        // Arrange
        Product product = createProduct(
                FIRST_PRODUCT_ID,
                "Gaming Laptop",
                "299.99",
                5
        );

        Order order = createOrder(List.of(createOrderItem(product, 2)));

        InvoiceGenerator failingInvoiceGenerator = mock(InvoiceGenerator.class);

        OrderProcessor failingOrderProcessor = new OrderProcessor(
                productRepository,
                orderRepository,
                invoiceRepository,
                failingInvoiceGenerator
        );

        when(productRepository.findById(FIRST_PRODUCT_ID))
                .thenReturn(Optional.of(product));

        when(failingInvoiceGenerator.generate(order))
                .thenThrow(new IllegalStateException(
                        "Invoice generation failed"
                ));

        // Act + Assert
        assertThatThrownBy(() -> failingOrderProcessor.process(order))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Invoice generation failed");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getAvailableQuantity()).isEqualTo(5);

        verify(productRepository, times(2)).save(product);
        verify(failingInvoiceGenerator).generate(order);
        verify(invoiceRepository, never()).save(any(Invoice.class));
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should reject order that is not NEW")
    public void shouldRejectOrderThatIsNotNew() {
        // Arrange
        Product product = createProduct(
                FIRST_PRODUCT_ID,
                "Gaming Laptop",
                "299.99",
                5
        );

        Order order = createOrder(List.of(
                createOrderItem(product, 1)
        ));

        order.markAsProcessing();

        // Act + Assert
        assertThatThrownBy(() -> orderProcessor.process(order))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot change order status from PROCESSING to PROCESSING");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(product.getAvailableQuantity()).isEqualTo(5);
    }

    private Customer createCustomer() {
        return new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );
    }

    private Product createProduct(
            UUID productId,
            String name,
            String price,
            int availableQuantity
    ) {
        return new Electronics(
                productId,
                name,
                new BigDecimal(price),
                availableQuantity
        );
    }

    private OrderItem createOrderItem(
            Product product,
            int quantity
    ) {
        return new OrderItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                quantity
        );
    }

    private Order createOrder(List<OrderItem> items) {
        return new Order(
                ORDER_ID,
                createCustomer(),
                items
        );
    }
}
