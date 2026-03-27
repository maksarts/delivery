package microarch.delivery.core.domain.model.order;

import java.util.UUID;

import microarch.delivery.core.domain.model.courier.Volume;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksimarts
 */
class OrderTest {

    @Test
    void orderCreatedWithCorrectStatusAndData() {
        UUID id = UUID.randomUUID();
        Location location = new Location(5, 5);
        Volume volume = new Volume(10);

        Order order = Order.createNew(id, location, volume);

        assertEquals(id, order.getId());
        assertEquals(location, order.getLocation());
        assertEquals(volume, order.getVolume());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void orderCanBeAssignedOnlyIfCreated() {
        Order order = Order.createNew(UUID.randomUUID(), new Location(6, 6), new Volume(5));

        assertEquals(OrderStatus.CREATED, order.getStatus());

        assertTrue(order.assign().isSuccess());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());

        assertTrue(order.assign().isFailure());
    }

    @Test
    void orderCanBeCompletedOnlyIfAssigned() {
        Order order = Order.createNew(UUID.randomUUID(), new Location(7, 7), new Volume(5));

        assertTrue(order.complete().isFailure());
        assertEquals(OrderStatus.CREATED, order.getStatus());

        assertTrue(order.assign().isSuccess());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());

        assertTrue(order.complete().isSuccess());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());

        assertTrue(order.complete().isFailure());
    }
}
