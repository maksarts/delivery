package microarch.delivery.core.domain.model.order;

import java.util.UUID;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.Getter;
import microarch.delivery.core.domain.model.courier.Volume;
import microarch.delivery.core.domain.model.shared_kernel.Location;

/**
 * @author maksimarts
 */
@Getter
public class Order extends Aggregate<UUID> {

    private final Location location;
    private final Volume volume;
    private OrderStatus status;

    private Order(UUID id, Location location, Volume volume) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.CREATED;
    }

    public static Order createNew(UUID id, Location location, Volume volume) {
        if (location == null || id == null || volume == null) {
            throw new IllegalArgumentException("Order must have id, location and volume");
        }
        return new Order(id, location, volume);
    }

    public UnitResult<Error> assign() {
        if (this.status != OrderStatus.CREATED) {
            return UnitResult.failure(Errors.wrongStatus(
                    "Order must be in CREATED status to be assigned, but was " + this.status));
        }
        this.status = OrderStatus.ASSIGNED;
        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (this.status != OrderStatus.ASSIGNED) {
            return UnitResult.failure(Errors.wrongStatus(
                    "Order must be in ASSIGNED status to be completed, but was " + this.status));
        }
        this.status = OrderStatus.COMPLETED;
        return UnitResult.success();
    }

    private static class Errors {
        static Error wrongStatus(String message) {
            return Error.of("wrong_status", message);
        }
    }
}
