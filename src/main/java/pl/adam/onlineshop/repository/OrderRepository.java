package pl.adam.onlineshop.repository;

import pl.adam.onlineshop.domain.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(Order order);

    Optional<Order> findById(UUID orderId);

    List<Order> findAll();

    boolean existsById(UUID orderId);
}
