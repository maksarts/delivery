package microarch.delivery.core.domain.model.courier;

import java.util.List;
import java.util.UUID;

import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksimarts
 */
class CourierTest {

    @Test
    void courierCanAssignOrdersWithinVolumeLimit() {
        Courier courier = Courier.createNew("Vasya", new Location(2, 2));
        Order order1 = Order.createNew(UUID.randomUUID(), new Location(3, 3), new Volume(10));
        Order order2 = Order.createNew(UUID.randomUUID(), new Location(4, 4), new Volume(9));

        assertTrue(courier.canAssign(order1).isSuccess());
        courier.assign(order1);
        assertTrue(courier.canAssign(order2).isSuccess());
    }

    @Test
    void courierCannotAssignOrdersExceedingVolumeLimit() {
        Courier courier = Courier.createNew("Vasya", new Location(2, 2));
        Order order1 = Order.createNew(UUID.randomUUID(), new Location(3, 3), new Volume(15));
        Order order2 = Order.createNew(UUID.randomUUID(), new Location(4, 4), new Volume(10));

        courier.assign(order1);
        assertFalse(courier.canAssign(order2).isSuccess());
        assertTrue(courier.assign(order2).isFailure());
    }

    @Test
    void courierCannotAssignSameOrderTwice() {
        Courier courier = Courier.createNew("Vasya", new Location(2, 2));
        Order order1 = Order.createNew(UUID.randomUUID(), new Location(3, 3), new Volume(15));

        courier.assign(order1);
        assertFalse(courier.canAssign(order1).isSuccess());
        assertTrue(courier.assign(order1).isFailure());
    }

    @Test
    void courierAssignmentContainsCorrectOrderId() {
        Courier courier = Courier.createNew("Vasya", new Location(2, 2));
        Order order = Order.createNew(UUID.randomUUID(), new Location(3, 3), new Volume(5));

        courier.assign(order);
        List<Assignment> assignments = courier.getAssignments();
        assertEquals(1, assignments.size());
        assertEquals(order.getId(), assignments.getFirst().getOrderId());
    }

    @Test
    void courierCanCompleteAssignmentOnlyIfCloseToOrder() {
        Courier courier = Courier.createNew("Vasya", new Location(2, 2));
        Order order = Order.createNew(UUID.randomUUID(), new Location(4, 4), new Volume(5));
        UUID assignmentId = courier.assign(order).getValueOrThrow();

        courier.moveTo(new Location(2, 2));
        assertTrue(courier.completeAssignment(assignmentId).isFailure());

        courier.moveTo(new Location(4, 3));
        assertTrue(courier.completeAssignment(assignmentId).isSuccess());
    }
}
