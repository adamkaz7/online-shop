package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;
import pl.adam.onlineshop.domain.order.OrderItem;
import pl.adam.onlineshop.domain.order.OrderStatus;
import pl.adam.onlineshop.domain.product.Product;
import pl.adam.onlineshop.exception.InsufficientStockException;
import pl.adam.onlineshop.exception.ProductNotFoundException;
import pl.adam.onlineshop.repository.InvoiceRepository;
import pl.adam.onlineshop.repository.OrderRepository;
import pl.adam.onlineshop.repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderProcessor {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceGenerator invoiceGenerator;

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
        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException(
                    "Only NEW order can be processed"
            );
        }
        order.markAsProcessing();

        try {
            Map<UUID, Product> products = findProducts(order);

            validateStock(order, products);
            decreaseStock(order, products);

            order.complete();
            orderRepository.save(order);

            Invoice invoice = invoiceGenerator.generate(order);
            invoiceRepository.save(invoice);

            return invoice;
        } catch (RuntimeException exception) {
            order.cancel();
            orderRepository.save(order);
            throw exception;
        }
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
}
