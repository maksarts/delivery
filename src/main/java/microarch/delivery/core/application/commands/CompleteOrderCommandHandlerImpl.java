package microarch.delivery.core.application.commands;

import java.util.NoSuchElementException;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author maksimarts
 */
@Service
@RequiredArgsConstructor
public class CompleteOrderCommandHandlerImpl implements CompleteOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;

    @Transactional
    @Override
    public UnitResult<Error> handle(CompleteOrderCommand command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        Courier courier = courierRepository.findById(command.courierId())
                .orElseThrow(() -> new NoSuchElementException("Courier not found"));

        UnitResult<Error> completeAssignmentResult = courier.completeAssignment(order.getId());
        if (completeAssignmentResult.isFailure()) {
            return completeAssignmentResult;
        }

        UnitResult<Error> completeOrderResult = order.complete();
        if (completeOrderResult.isFailure()) {
            return completeOrderResult;
        }

        orderRepository.save(order);
        courierRepository.save(courier);
        return UnitResult.success();
    }
}
