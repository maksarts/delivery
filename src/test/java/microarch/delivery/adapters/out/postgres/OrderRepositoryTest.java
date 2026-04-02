package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import microarch.delivery.core.domain.model.courier.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksimarts
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveOrder() {
        Order order = Order.createNew(UUID.randomUUID(), new Location(1, 1), new Volume(1));
        orderRepository.save(order);

        Optional<Order> foundO = orderRepository.findById(order.getId());
        assertTrue(foundO.isPresent());

        Order found = foundO.get();
        assertEquals(order.getId(), found.getId());
        assertEquals(order.getLocation(), found.getLocation());
        assertEquals(order.getVolume(), found.getVolume());
        assertEquals(order.getStatus(), found.getStatus());

        found.assign();
        orderRepository.save(found);

        Optional<Order> found2O = orderRepository.findById(order.getId());
        assertTrue(found2O.isPresent());

        Order found2 = found2O.get();
        assertEquals(found.getId(), found2.getId());
        assertEquals(found.getLocation(), found2.getLocation());
        assertEquals(found.getVolume(), found2.getVolume());
        assertEquals(found.getStatus(), found2.getStatus());
    }

    @Test
    void findCreated() {
        Order order = Order.createNew(UUID.randomUUID(), new Location(1, 1), new Volume(1));
        orderRepository.save(order);

        Optional<Order> foundO = orderRepository.findCreated();

        assertTrue(foundO.isPresent());
        assertEquals(order.getId(), foundO.get().getId());
        assertEquals(OrderStatus.CREATED, foundO.get().getStatus());

        Order found = foundO.get();
        found.assign();
        orderRepository.save(found);

        Optional<Order> found2O = orderRepository.findCreated();

        assertFalse(found2O.isPresent());
    }

    @Test
    void findAllAssigned() {
        Order order1 = Order.createNew(UUID.randomUUID(), new Location(1, 1), new Volume(1));
        Order order2 = Order.createNew(UUID.randomUUID(), new Location(2, 2), new Volume(2));

        orderRepository.save(order1);
        orderRepository.save(order2);

        order1.assign();
        orderRepository.save(order1);

        List<Order> assignedOrders = orderRepository.findAllAssigned();

        assertEquals(1, assignedOrders.size());
        assertEquals(order1.getId(), assignedOrders.get(0).getId());
        assertEquals(OrderStatus.ASSIGNED, assignedOrders.get(0).getStatus());
    }

}
