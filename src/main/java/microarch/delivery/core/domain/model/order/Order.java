package microarch.delivery.core.domain.model.order;

import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.courier.Volume;
import microarch.delivery.core.domain.model.shared_kernel.Location;

/**
 * @author maksimarts
 */
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
public class Order extends Aggregate<UUID> {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "location_x")),
            @AttributeOverride(name = "y", column = @Column(name = "location_y"))
    })
    private Location location;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "volume_value"))
    })
    private Volume volume;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
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
