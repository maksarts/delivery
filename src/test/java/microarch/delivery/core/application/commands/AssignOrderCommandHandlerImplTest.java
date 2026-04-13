package microarch.delivery.core.application.commands;

import java.util.List;
import java.util.Optional;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.services.DispatcherService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * @author maksimarts
 */
class AssignOrderCommandHandlerImplTest {
    private CourierRepository courierRepository;
    private OrderRepository orderRepository;
    private DispatcherService dispatcherService;
    private AssignOrderCommandHandlerImpl handler;

    @BeforeEach
    void setUp() {
        courierRepository = mock(CourierRepository.class);
        orderRepository = mock(OrderRepository.class);
        dispatcherService = mock(DispatcherService.class);
        handler = new AssignOrderCommandHandlerImpl(courierRepository, orderRepository, dispatcherService);
    }

    @Test
    void shouldReturnFailureWhenNoCreatedOrdersExist() {
        when(orderRepository.findCreated()).thenReturn(Optional.empty());

        UnitResult<Error> result = handler.handle(new AssignOrderCommand());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("no_created_orders");
    }

    @Test
    void shouldNotInteractWithCouriersWhenNoCreatedOrdersExist() {
        when(orderRepository.findCreated()).thenReturn(Optional.empty());

        handler.handle(new AssignOrderCommand());

        verifyNoInteractions(courierRepository);
        verifyNoInteractions(dispatcherService);
    }

    @Test
    void shouldReturnFailureWhenDispatcherServiceFails() {
        Order order = mock(Order.class);
        List<Courier> couriers = List.of(mock(Courier.class));
        Error error = Error.of("no_couriers_available", "No couriers available");

        when(orderRepository.findCreated()).thenReturn(Optional.of(order));
        when(courierRepository.findAll()).thenReturn(couriers);
        when(dispatcherService.assignOrderOnBestCourier(order, couriers))
                .thenReturn(Result.failure(error));

        UnitResult<Error> result = handler.handle(new AssignOrderCommand());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(error);
    }

    @Test
    void shouldSaveCourierAndOrderWhenAssignmentSucceeds() {
        Order order = mock(Order.class);
        Courier courier = mock(Courier.class);
        List<Courier> couriers = List.of(courier);

        when(orderRepository.findCreated()).thenReturn(Optional.of(order));
        when(courierRepository.findAll()).thenReturn(couriers);
        when(dispatcherService.assignOrderOnBestCourier(order, couriers))
                .thenReturn(Result.success(courier));

        handler.handle(new AssignOrderCommand());

        verify(courierRepository).save(courier);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldReturnSuccessWhenAssignmentSucceeds() {
        Order order = mock(Order.class);
        Courier courier = mock(Courier.class);
        List<Courier> couriers = List.of(courier);

        when(orderRepository.findCreated()).thenReturn(Optional.of(order));
        when(courierRepository.findAll()).thenReturn(couriers);
        when(dispatcherService.assignOrderOnBestCourier(order, couriers))
                .thenReturn(Result.success(courier));

        UnitResult<Error> result = handler.handle(new AssignOrderCommand());

        assertThat(result.isSuccess()).isTrue();
    }
}
