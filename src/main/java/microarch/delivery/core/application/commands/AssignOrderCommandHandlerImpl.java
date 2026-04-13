package microarch.delivery.core.application.commands;

import java.util.List;
import java.util.Optional;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.services.DispatcherService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author maksimarts
 */
@Service
@RequiredArgsConstructor
public class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final DispatcherService dispatcherService;

    @Transactional
    @Override
    public UnitResult<Error> handle(AssignOrderCommand command) {
        Optional<Order> orderO = orderRepository.findCreated();
        if (orderO.isEmpty()) {
            return UnitResult.failure(Error.of("no_created_orders", "No created orders yet"));
        }
        Order order = orderO.get();

        List<Courier> couriers = courierRepository.findAll();

        Result<Courier, Error> result = dispatcherService.assignOrderOnBestCourier(order, couriers);
        if (result.isFailure()) {
            return UnitResult.failure(result.getError());
        }
        Courier assignedToCourier = result.getValue();

        courierRepository.save(assignedToCourier);
        orderRepository.save(order);
        return UnitResult.success();
    }
}
