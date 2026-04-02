package microarch.delivery.core.domain.model.courier;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.shared_kernel.Location;

/**
 * @author maksimarts
 */
@Entity
@Table(name = "assignments")
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
public class Assignment extends BaseEntity<UUID> {

    @Column(name = "order_id")
    private UUID orderId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "volume_value"))
    })
    private Volume volume;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "location_x")),
            @AttributeOverride(name = "y", column = @Column(name = "location_y"))
    })
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    private Assignment(UUID id, UUID orderId, Volume volume, Location location, Status status) {
        super(id);
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public static Assignment createNew(Order order) {
        return createNew(order.getId(), order.getVolume(), order.getLocation());
    }

    public static Assignment createNew(UUID orderId, Volume volume, Location location) {
        if (orderId == null || volume == null || location == null) {
            throw new IllegalArgumentException("Assignment must have orderId, volume and location");
        }
        return new Assignment(UUID.randomUUID(), orderId, volume, location, Status.ASSIGNED);
    }

    public UnitResult<Error> complete(Location currLocation) {
        if (currLocation == null) {
            throw new IllegalArgumentException("Current location must be specified");
        }
        if (location.chebyshevDistanceTo(currLocation) > 1) {
            return UnitResult.failure(
                    Error.of("invalid_location", "Current location is too far from assignment location"));
        }
        if (status == Status.COMPLETED) {
            return UnitResult.failure(
                    Error.of("already_completed", "Assignment is already completed"));
        }
        status = Status.COMPLETED;
        return UnitResult.success();
    }

    public Assignment clone() {
        return new Assignment(id, orderId, volume, location, status);
    }

    public enum Status {
        ASSIGNED, COMPLETED;

        @JsonCreator
        public static Status fromValue(String value) {
            return Status.valueOf(value.toUpperCase());
        }

        @JsonValue
        public String toValue() {
            return name().toLowerCase();
        }

    }
}
