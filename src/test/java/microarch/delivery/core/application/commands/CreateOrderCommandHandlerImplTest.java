package microarch.delivery.core.application.commands;

import java.util.UUID;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author maksimarts
 */
class CreateOrderCommandHandlerImplTest {
    private OrderRepository orderRepository;
    private CreateOrderCommandHandlerImpl handler;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        handler = new CreateOrderCommandHandlerImpl(orderRepository);
    }

    @Test
    void shouldSaveOrderOnHandle() {
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(), "test", "test", "test", "test", "test", 1);

        handler.handle(command);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldReturnSuccessOnHandle() {
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(), "test", "test", "test", "test", "test", 1);

        UnitResult<Error> result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
    }
}
