package microarch.delivery.core.domain.services;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.extern.slf4j.Slf4j;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.stereotype.Service;

/**
 * @author maksimarts
 */
@Service
@Slf4j
public class DispatcherServiceImpl implements DispatcherService {

    @Override
    public Result<Courier, Error> assignOrderOnBestCourier(Order order, List<Courier> couriers) {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(couriers, "couriers");

        if (order.getStatus() != OrderStatus.CREATED) {
            return Result.failure(
                    Error.of("invalid_order_status", "OrderStatus = CREATED expected, but was " + order.getStatus()));
        }

        Result<Courier, Error> selectBestCourierResult = selectBestCourier(order, couriers);
        if (selectBestCourierResult.isFailure()) {
            return Result.failure(selectBestCourierResult.getError());
        }

        Courier bestCourier = selectBestCourierResult.getValueOrThrow();

        Result<UUID, Error> courierAssignResult = bestCourier.assign(order);
        if (courierAssignResult.isFailure()) {
            return Result.failure(courierAssignResult.getError());
        }

        UnitResult<Error> orderAssignResult = order.assign();
        if (orderAssignResult.isFailure()) {
            return Result.failure(orderAssignResult.getError());
        }

        log.info("Order {} assigned to courier {}, assignmentId = {}",
                order.getId(), bestCourier.getId(), courierAssignResult.getValueOrThrow());
        return Result.success(bestCourier);
    }

    private Result<Courier, Error> selectBestCourier(Order order, List<Courier> couriers) {
        Optional<Courier> bestCourierO = couriers.stream()
                .filter(courier -> courier.canAssign(order).isSuccess())
                .min(Comparator.comparing(courier -> courier.getCurrLocation().distanceTo(order.getLocation())));

        return bestCourierO.map(Result::success)
                .orElseGet(() -> Result.failure(Error.of("no_available_couriers", "No available couriers")));
    }
}
