package microarch.delivery.core.domain.services;

import java.util.List;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

/**
 * @author maksimarts
 */
public interface DispatcherService {
    Result<Courier, Error> assignOrderOnBestCourier(Order order, List<Courier> couriers);
}
