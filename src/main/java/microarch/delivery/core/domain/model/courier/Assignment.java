package microarch.delivery.core.domain.model.courier;

import java.util.UUID;

import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.Getter;
import microarch.delivery.core.domain.model.shared_kernel.Location;

/**
 * @author maksimarts
 */
@Getter
public class Assignment extends BaseEntity<UUID> {
    private final UUID id;
    private final UUID orderId;
    private final Volume volume;
    private final Location location;
    private Status status;

    public Assignment(UUID orderId, Volume volume, Location location) {
        if (orderId == null || volume == null || location == null) {
            throw new IllegalArgumentException("Assignment must have orderId, volume and location");
        }
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = Status.ASSIGNED;
    }

    public UnitResult<Error> complete(Location currLocation) {
        if (currLocation == null) {
            return UnitResult.failure(
                    Error.of("invalid_location", "Current location cannot be null"));
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

    public enum Status {
        ASSIGNED,
        COMPLETED
    }
}
