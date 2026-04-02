package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Repository;

/**
 * @author maksimarts
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public void save(Order order) {
        jpaOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaOrderRepository.findById(id);
    }

    @Override
    public Optional<Order> findCreated() {
        return jpaOrderRepository.findOrdersByStatus(OrderStatus.CREATED).stream().findAny();
    }

    @Override
    public List<Order> findAllAssigned() {
        return jpaOrderRepository.findOrdersByStatus(OrderStatus.ASSIGNED);
    }
}
