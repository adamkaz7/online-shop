package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.InsufficientStockException;
import pl.adam.onlineshop.exception.ProductNotFoundException;
import pl.adam.onlineshop.repository.InvoiceRepository;
import pl.adam.onlineshop.repository.OrderRepository;
import pl.adam.onlineshop.repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Design pattern: Facade
// Provides a single entry point for stock validation,
// order persistence and invoice generation.
public class OrderProcessor {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceGenerator invoiceGenerator;
    private final Object stockLock = new Object();

    public OrderProcessor(
            @NonNull ProductRepository productRepository,
            @NonNull OrderRepository orderRepository,
            @NonNull InvoiceRepository invoiceRepository,
            @NonNull InvoiceGenerator invoiceGenerator
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceGenerator = invoiceGenerator;
    }

    public Invoice process(@NonNull Order order) {
        order.markAsProcessing();

        Map<UUID, Product> products = new HashMap<>();
        boolean stockDecreased = false;
        Invoice invoice;

        try {
            products = findProducts(order);

            synchronized (stockLock) {
                validateStock(order, products);
                decreaseStock(order, products);
                stockDecreased = true;
            }

            invoice = invoiceGenerator.generate(order);
            invoiceRepository.save(invoice);
        } catch (RuntimeException exception) {
            if (stockDecreased) {
                synchronized (stockLock) {
                    restoreStock(order, products);
                }
            }

            order.cancel();
            orderRepository.save(order);
            throw exception;
        }

        order.complete();
        orderRepository.save(order);

        return invoice;
    }

    private Map<UUID, Product> findProducts(Order order) {
        Map<UUID, Product> productsById = new HashMap<>();

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() ->
                            new ProductNotFoundException(item.productId())
                    );
            productsById.put(item.productId(), product);
        }
        return productsById;
    }

    private void validateStock(
            Order order,
            Map<UUID, Product> productsById
    ) {
        for (OrderItem item : order.getItems()) {
            Product product = productsById.get(item.productId());
            if (!product.hasAvailableQuantity(item.quantity())) {
                throw new InsufficientStockException(item.productId());
            }
        }
    }

    private void decreaseStock(
            Order order,
            Map<UUID, Product> productsById
    ) {
        for (OrderItem item : order.getItems()) {
            Product product = productsById.get(item.productId());

            product.decreaseAvailableQuantity(item.quantity());
            productRepository.save(product);
        }
    }

    private void restoreStock(Order order, Map<UUID, Product> productsById) {
        for (OrderItem item : order.getItems()) {
            Product product = productsById.get(item.productId());

            product.increaseAvailableQuantity(item.quantity());
            productRepository.save(product);
        }
    }
}
