package pl.adam.onlineshop.repository;

import lombok.NonNull;
import pl.adam.onlineshop.domain.order.Order;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    @Override
    public void save(@NonNull Order order) {
        orders.put(order.getOrderId(), order);
    }

    @Override
    public Optional<Order> findById(@NonNull UUID orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<Order> findAll() {
        return List.copyOf(orders.values());
    }

    @Override
    public boolean existsById(@NonNull UUID orderId) {
        return orders.containsKey(orderId);
    }
}
