package pl.adam.onlineshop.service;

import lombok.NonNull;
import pl.adam.onlineshop.domain.invoice.Invoice;
import pl.adam.onlineshop.domain.order.Order;

import java.util.List;
import java.util.concurrent.*;

public class OrderBatchProcessor implements AutoCloseable {
    private static final long TERMINATION_TIMEOUT_SECONDS = 30;

    private final OrderProcessor orderProcessor;
    private final ExecutorService executorService;

    public OrderBatchProcessor(@NonNull OrderProcessor orderProcessor, int threadCount) {
        validateThreadCount(threadCount);

        this.orderProcessor = orderProcessor;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    public List<OrderResult> processSync(@NonNull List<Order> orders) {
        return orders.stream()
                .map(this::processOrderSafely)
                .toList();
    }

    public CompletableFuture<List<OrderResult>> processAsync(@NonNull List<Order> orders) {
        List<CompletableFuture<OrderResult>> futures = orders.stream()
                .map(order
                        -> CompletableFuture.supplyAsync(
                                () -> orderProcessor.process(order), executorService)
                        .handle((invoice, exception) ->
                                createOrderResult(
                                        order,
                                        invoice,
                                        exception
                                )))
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

        try {
            if (!executorService.awaitTermination(
                    TERMINATION_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            )) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private OrderResult processOrderSafely(Order order) {
        try {
            Invoice invoice = orderProcessor.process(order);
            return OrderResult.success(order, invoice);
        } catch (RuntimeException exception) {
            return OrderResult.failure(order, exception);
        }
    }

    private OrderResult createOrderResult(Order order, Invoice invoice, Throwable exception) {
        if (exception == null) {
            return OrderResult.success(order, invoice);
        }

        return OrderResult.failure(
                order,
                unwrapException(exception)
        );
    }

    private Throwable unwrapException(Throwable exception) {
        if (exception instanceof CompletionException && exception.getCause() != null) {
            return exception.getCause();
        }

        return exception;
    }

    private static void validateThreadCount(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be greater than zero");
        }
    }
}
