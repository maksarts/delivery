package microarch.delivery.core.application.commands;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.courier.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author maksimarts
 */
class CompleteOrderCommandHandlerImplTest {
    private OrderRepository orderRepository;
    private CourierRepository courierRepository;
    private CompleteOrderCommandHandlerImpl handler;

    private UUID orderId;
    private UUID courierId;
    private Order order;
    private Courier courier;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        courierRepository = mock(CourierRepository.class);
        handler = new CompleteOrderCommandHandlerImpl(orderRepository, courierRepository);

        orderId = UUID.randomUUID();

        order = Order.createNew(orderId, new Location(1, 1), new Volume(1));
        courier = Courier.createNew("John", new Location(1, 1));
        courierId = courier.getId();
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        assertThrows(
                NoSuchElementException.class,
                () -> handler.handle(new CompleteOrderCommand(courierId, orderId))
        );
    }

    @Test
    void shouldThrowWhenCourierNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> handler.handle(new CompleteOrderCommand(courierId, orderId))
        );
    }

    @Test
    void shouldThrowWhenCourierHasNoAssignmentForOrder() {
        // Курьер существует, но у него нет assignment для этого заказа
        // order не был назначен на курьера — completeAssignment бросит NoSuchElementException
        order.assign();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        assertThrows(
                NoSuchElementException.class,
                () -> handler.handle(new CompleteOrderCommand(courierId, orderId))
        );
    }

    @Test
    void shouldReturnFailureWhenOrderIsNotAssigned() {
        // Курьер берёт заказ через assign() — получает assignment
        // Но сам Order остаётся в статусе CREATED, а не ASSIGNED
        // Поэтому order.complete() вернёт failure
        courier.assign(order);

        // order намеренно НЕ переводим в ASSIGNED через order.assign()
        // чтобы order.complete() вернул failure (он в статусе CREATED)

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        UnitResult<Error> result = handler.handle(new CompleteOrderCommand(courierId, orderId));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("wrong_status");
    }

    @Test
    void shouldReturnSuccessWhenOrderAndCourierAreValid() {
        // Полный happy path: заказ назначен на курьера, оба в корректном статусе
        courier.assign(order);
        order.assign();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        UnitResult<Error> result = handler.handle(new CompleteOrderCommand(courierId, orderId));

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldSaveOrderOnSuccess() {
        courier.assign(order);
        order.assign();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        handler.handle(new CompleteOrderCommand(courierId, orderId));

        verify(orderRepository).save(order);
    }

    @Test
    void shouldSaveCourierOnSuccess() {
        courier.assign(order);
        order.assign();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        handler.handle(new CompleteOrderCommand(courierId, orderId));

        verify(courierRepository).save(courier);
    }

    @Test
    void shouldNotSaveWhenOrderIsNotAssigned() {
        courier.assign(order);
        // order намеренно НЕ переводим в ASSIGNED

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        handler.handle(new CompleteOrderCommand(courierId, orderId));

        verify(orderRepository, never()).save(any());
        verify(courierRepository, never()).save(any());
    }

    @Test
    void shouldSetOrderStatusToCompletedOnSuccess() {
        courier.assign(order);
        order.assign();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        handler.handle(new CompleteOrderCommand(courierId, orderId));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldRemoveAssignmentFromCourierOnSuccess() {
        courier.assign(order);
        order.assign();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        handler.handle(new CompleteOrderCommand(courierId, orderId));

        // После завершения у курьера не должно остаться assignments
        assertThat(courier.getAssignments()).isEmpty();
    }
}
