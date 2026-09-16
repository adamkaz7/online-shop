package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderBatchProcessor implements AutoCloseable {
    private final OrderProcessor orderProcessor;
    private final ExecutorService executorService;

    public OrderBatchProcessor(@NonNull OrderProcessor orderProcessor, int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be greater than zero");
        }

        this.orderProcessor = orderProcessor;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    public List<Invoice> processSync(@NonNull List<Order> orders) {
        return orders.stream()
                .map(orderProcessor::process)
                .toList();
    }

    public CompletableFuture<List<Invoice>> processAsync(@NonNull List<Order> orders) {
        List<CompletableFuture<Invoice>> futures = orders.stream()
                .map(order
                        -> CompletableFuture.supplyAsync(
                        () -> orderProcessor.process(order), executorService))
                .toList();

        CompletableFuture<Void> allOrders =
                CompletableFuture.allOf(
                        futures.toArray(
                                CompletableFuture[]::new
                        )
                );

        return allOrders.thenApply(ignored -> futures.stream()
                .map(CompletableFuture::join)
                .toList());
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
