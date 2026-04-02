package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.UUID;

import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author maksimarts
 */
public interface JpaOrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findOrdersByStatus(OrderStatus status);
}
