package microarch.delivery.core.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import microarch.delivery.core.domain.model.order.Order;

/**
 * @author maksimarts
 */
public interface OrderRepository {
    void save(Order order);

    Optional<Order> findById(UUID id);

    Optional<Order> findCreated();

    List<Order> findAllAssigned();

    List<Order> findAllNotCompleted();
}
