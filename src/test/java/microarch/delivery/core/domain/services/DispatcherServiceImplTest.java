package microarch.delivery.core.domain.services;

import java.util.List;
import java.util.UUID;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.courier.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksimarts
 */
class DispatcherServiceImplTest {
    @Test
    void assignsOrderToNearestAvailableCourier() {
        // Arrange
        Location orderLoc = new Location(5, 5);
        Order order = Order.createNew(UUID.randomUUID(), orderLoc, new Volume(3));

        Courier far = Courier.createNew("A", new Location(1, 1));
        Courier expectedBest = Courier.createNew("B", new Location(4, 4));
        Courier nearFull = Courier.createNew("C", new Location(5, 5));
        nearFull.assign(createDummyMaxVolOrder());

        List<Courier> couriers = List.of(far, expectedBest, nearFull);

        DispatcherServiceImpl dispatcher = new DispatcherServiceImpl();

        // Act
        Result<Courier, Error> result = dispatcher.assignOrderOnBestCourier(order, couriers);

        // Assert
        assertTrue(result.isSuccess());
        Courier assigned = result.getValueOrThrow();

        assertEquals(expectedBest.getId(), assigned.getId(), "Назначен должен быть ближайший доступный курьер");
        assertTrue(assigned.getAssignments().stream().anyMatch(a -> a.getOrderId().equals(order.getId())));
        assertEquals(1, assigned.getAssignments().size());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
    }

    @Test
    void doesNotAssignWhenAllCouriersAreFull() {
        // Arrange
        Location orderLoc = new Location(5, 5);
        Order order = Order.createNew(UUID.randomUUID(), orderLoc, new Volume(5));
        Courier full1 = Courier.createNew("A", new Location(1, 1));
        full1.assign(createDummyMaxVolOrder());
        Courier full2 = Courier.createNew("B", new Location(5, 4));
        full2.assign(createDummyMaxVolOrder());

        List<Courier> couriers = List.of(full1, full2);

        DispatcherServiceImpl dispatcher = new DispatcherServiceImpl();

        // Act
        Result<Courier, Error> result = dispatcher.assignOrderOnBestCourier(order, couriers);

        // Assert
        assertTrue(result.isFailure());
        assertEquals("no_available_couriers", result.getError().getCode());
    }

    @Test
    void doesNotAssignWhenCourierListIsEmpty() {
        // Arrange
        Order order = Order.createNew(UUID.randomUUID(), new Location(2, 2), new Volume(1));
        DispatcherServiceImpl dispatcher = new DispatcherServiceImpl();

        // Act
        Result<Courier, Error> result = dispatcher.assignOrderOnBestCourier(order, List.of());

        // Assert
        assertTrue(result.isFailure());
        assertEquals("no_available_couriers", result.getError().getCode());
    }

    @Test
    void doesNotAssignIfOrderNotCreated() {
        // Arrange
        Order alreadyAssignedOrder = Order.createNew(UUID.randomUUID(), new Location(2, 2), new Volume(1));
        alreadyAssignedOrder.assign();
        Courier courier = Courier.createNew("Test", new Location(2, 2));
        DispatcherServiceImpl dispatcher = new DispatcherServiceImpl();

        // Act
        Result<Courier, Error> result = dispatcher.assignOrderOnBestCourier(alreadyAssignedOrder, List.of(courier));

        // Assert
        assertTrue(result.isFailure());
        assertEquals("invalid_order_status", result.getError().getCode());
    }

    @Test
    void doesNotAssignIfCourierRefusesAssignment() {
        // Arrange
        Order tooBigVolume = Order.createNew(UUID.randomUUID(), new Location(2, 2), new Volume(30));
        Courier courier = Courier.createNew("Test", new Location(2, 2));
        DispatcherServiceImpl dispatcher = new DispatcherServiceImpl();

        // Act
        Result<Courier, Error> result = dispatcher.assignOrderOnBestCourier(tooBigVolume, List.of(courier));

        // Assert
        assertTrue(result.isFailure());
        assertEquals("no_available_couriers", result.getError().getCode());
    }

    private Order createDummyMaxVolOrder() {
        return Order.createNew(UUID.randomUUID(), new Location(7, 7), new Volume(20));
    }
}
