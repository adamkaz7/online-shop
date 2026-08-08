package pl.adam.onlineshop.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.adam.onlineshop.domain.customer.Customer;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.order.OrderStatus;
import pl.adam.onlineshop.domain.product.Electronics;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.InsufficientStockException;
import pl.adam.onlineshop.repository.InMemoryInvoiceRepository;
import pl.adam.onlineshop.repository.InMemoryOrderRepository;
import pl.adam.onlineshop.repository.InMemoryProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class OrderBatchProcessorTest {
    private static final UUID CUSTOMER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000001"
    );

    private static final UUID PRODUCT_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000002"
    );

    private static final UUID FIRST_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000011"
    );

    private static final UUID SECOND_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000012"
    );

    private static final UUID THIRD_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000013"
    );

    private static final UUID FOURTH_ORDER_ID = UUID.fromString(
            "00000000-0000-0000-0000-000000000014"
    );

    private static final int ORDER_COUNT = 100;
    private static final long DELAY = 100;

    private InMemoryProductRepository productRepository;
    private InMemoryOrderRepository orderRepository;
    private InMemoryInvoiceRepository invoiceRepository;
    private OrderProcessor orderProcessor;

    @BeforeEach
    public void setUp() {
        initializedOrderProcessor(new InvoiceGenerator());
    }

    @Test
    @DisplayName("Should process multiple orders asynchronously")
    public void shouldProcessMultipleOrdersAsynchronously() {
        // Arrange
        Product product = createProduct(10);
        productRepository.save(product);

        List<Order> orders = List.of(
                createOrder(FIRST_ORDER_ID, product, 2),
                createOrder(SECOND_ORDER_ID, product, 2),
                createOrder(THIRD_ORDER_ID, product, 2),
                createOrder(FOURTH_ORDER_ID, product, 2)
        );

        // Act
        List<Invoice> invoices;

        try (OrderBatchProcessor batchProcessor = new OrderBatchProcessor(orderProcessor, 4)) {
            CompletableFuture<List<Invoice>> future = batchProcessor.processAsync(orders);
            invoices = future.join();
        }

        // Assert
        assertThat(orders)
                .extracting(Order::getStatus)
                .containsOnly(OrderStatus.COMPLETED);

        assertThat(product.getAvailableQuantity()).isEqualTo(2);
        assertThat(orderRepository.findAll()).containsExactlyInAnyOrderElementsOf(orders);
        assertThat(invoiceRepository.findAll()).hasSize(4);
    }

    @Test
    @DisplayName("Should prevent overselling when orders compete for stock")
    public void shouldPreventOversellingWhenOrdersCompeteForStock() {
        // Arrange
        Product product = createProduct(1);
        productRepository.save(product);

        Order firstOrder = createOrder(FIRST_ORDER_ID, product, 1);
        Order secondOrder = createOrder(SECOND_ORDER_ID, product, 1);

        List<Order> orders = List.of(
                firstOrder,
                secondOrder
        );

        // Act + Assert
        try (OrderBatchProcessor batchProcessor = new OrderBatchProcessor(orderProcessor, 3)) {
            assertThatThrownBy(() -> batchProcessor.processAsync(orders).join())
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(InsufficientStockException.class);
        }

        assertThat(orders)
                .extracting(Order::getStatus)
                .containsExactlyInAnyOrder(OrderStatus.COMPLETED, OrderStatus.CANCELLED);

        assertThat(product.getAvailableQuantity()).isZero();
        assertThat(orderRepository.findAll()).containsExactlyInAnyOrder(firstOrder, secondOrder);
        assertThat(invoiceRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Should process orders faster asynchronously")
    public void shouldProcessOrdersAsynchronously() {
        // Arrange - sync
        initializedOrderProcessor(new DelayedInvoiceGenerator());

        Product syncProduct = createProduct(ORDER_COUNT);
        productRepository.save(syncProduct);

        List<Order> syncOrders = createOrders(syncProduct, ORDER_COUNT);

        // Act - sync
        long syncStart = System.nanoTime();

        List<Invoice> syncInvoices;

        try (OrderBatchProcessor batchProcessor = new OrderBatchProcessor(orderProcessor, 4)) {
            syncInvoices = batchProcessor.processSync(syncOrders);
        }

        long syncDuration = System.nanoTime() - syncStart;

        // Arrange - async
        initializedOrderProcessor(new DelayedInvoiceGenerator());

        Product asyncProduct = createProduct(ORDER_COUNT);
        productRepository.save(asyncProduct);

        List<Order> asyncOrders = createOrders(asyncProduct, ORDER_COUNT);

        // Act - async
        long asyncStart = System.nanoTime();
        List<Invoice> asyncInvoices;

        try (OrderBatchProcessor batchProcessor = new OrderBatchProcessor(orderProcessor, 4)) {
            asyncInvoices = batchProcessor.processAsync(asyncOrders).join();
        }

        long asyncDuration = System.nanoTime() - asyncStart;

        // Assert
        assertThat(syncInvoices).hasSize(ORDER_COUNT);
        assertThat(asyncInvoices).hasSize(ORDER_COUNT);

        log.info("Should process orders faster asynchronously:");
        log.info("Synchronous: {} ms, asynchronous: {} ms",
                TimeUnit.NANOSECONDS.toMillis(syncDuration),
                TimeUnit.NANOSECONDS.toMillis(asyncDuration)
        );

        assertThat(asyncDuration).isLessThan(syncDuration / 2);
    }

    private Product createProduct(int availableQuantity) {
        return new Electronics(
                PRODUCT_ID,
                "Gaming Laptop",
                new BigDecimal("199.99"),
                availableQuantity
        );
    }

    private Order createOrder(
            UUID orderId,
            Product product,
            int quantity
    ) {
        Customer customer = new Customer(
                CUSTOMER_ID,
                "Jan Kowalski"
        );

        OrderItem item = new OrderItem(
                product.getId(),
                product.getName(),
                product.getPrice(),
                quantity
        );

        return new Order(
                orderId,
                customer,
                List.of(item)
        );
    }

    private void initializedOrderProcessor(InvoiceGenerator invoiceGenerator) {
        productRepository = new InMemoryProductRepository();
        orderRepository = new InMemoryOrderRepository();
        invoiceRepository = new InMemoryInvoiceRepository();

        orderProcessor = new OrderProcessor(
                productRepository,
                orderRepository,
                invoiceRepository,
                invoiceGenerator
        );
    }

    private List<Order> createOrders(Product product, int orderCount) {
        return IntStream.range(0, orderCount)
                .mapToObj(index -> createOrder(
                        UUID.randomUUID(),
                        product,
                        1
                ))
                .toList();
    }

    private static class DelayedInvoiceGenerator extends InvoiceGenerator {
        @Override
        public Invoice generate(@NonNull Order order) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();

                throw new IllegalStateException("Invoice generation was interrupted", exception);
            }
            return super.generate(order);
        }
    }
}
