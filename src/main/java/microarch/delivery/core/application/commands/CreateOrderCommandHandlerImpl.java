package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * @author maksimarts
 */
@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;

    @Override
    public UnitResult<Error> handle(CreateOrderCommand command) {
        Order order = Order.createNew(
                command.getOrderId(),
                new Location(1, 1), //TODO использовать сервис Geo в следующих модулях
                command.getVolume()
        );
        orderRepository.save(order);
        return UnitResult.success();
    }
}
