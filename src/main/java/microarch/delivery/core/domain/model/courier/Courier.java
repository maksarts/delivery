package microarch.delivery.core.domain.model.courier;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Result;
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
@Table(name = "couriers")
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
public class Courier extends Aggregate<UUID> {

    private static final Volume MAX_VOLUME = new Volume(20);

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "courier_id", nullable = false)
    private final List<Assignment> assignments = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "location_x")),
            @AttributeOverride(name = "y", column = @Column(name = "location_y"))
    })
    private Location currLocation;

    private Courier(UUID id, String name, Location currLocation) {
        super(id);
        this.currLocation = currLocation;
        this.name = name;
    }

    public static Courier createNew(String name, Location location) {
        if (name == null || location == null) {
            throw new IllegalArgumentException("Courier must have name and location");
        }
        return new Courier(UUID.randomUUID(), name, location);
    }

    public List<Assignment> getAssignments() {
        // deep copy
        return assignments.stream().map(Assignment::clone).toList();
    }

    public UnitResult<Error> canAssign(Order order) {
        if (!hasFreeVolumeForOrder(order)) {
            return UnitResult.failure(Error.of("max_volume_reached", "Max volume reached"));
        }
        if (assignments.stream().anyMatch(a -> a.getOrderId().equals(order.getId()))) {
            return UnitResult.failure(Error.of("assignment_already_exists", "Order already assigned to this courier"));
        }
        return UnitResult.success();
    }

    public Result<UUID, Error> assign(Order order) {
        UnitResult<Error> canAssign = canAssign(order);
        if (canAssign.isFailure()) {
            return Result.failure(canAssign.getError());
        }

        Assignment assignment = Assignment.createNew(order);
        assignments.add(assignment);
        return Result.success(assignment.getId());
    }

    public UnitResult<Error> completeAssignment(UUID orderId) {
        Assignment assignment = assignments.stream()
                .filter(a -> a.getOrderId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Assignment not found"));

        UnitResult<Error> result = assignment.complete(currLocation);
        if (result.isFailure()) {
            return result;
        }
        assignments.remove(assignment);
        return UnitResult.success();
    }

    public UnitResult<Error> moveTo(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location must not be null");
        }
        currLocation = location;
        return UnitResult.success();
    }

    private boolean hasFreeVolumeForOrder(Order order) {
        return getCurrentVolume().add(order.getVolume()).isLowerOrEqualsThan(MAX_VOLUME);
    }

    private Volume getCurrentVolume() {
        return assignments.stream()
                .filter(a -> a.getStatus() == Assignment.Status.ASSIGNED)
                .map(Assignment::getVolume)
                .reduce(Volume::add)
                .orElse(new Volume(0));
    }
}
