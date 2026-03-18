package microarch.delivery.core.domain.model.courier;

import java.util.UUID;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksimarts
 */
class AssignmentTest {

    private final UUID orderId = UUID.randomUUID();
    private final Volume volume = new Volume(10);
    private final Location location = new Location(3, 5);

    @Test
    void constructor_WithValidParameters() {
        Assignment assignment = createAssignment();

        assertNotNull(assignment.getId());
        assertEquals(orderId, assignment.getOrderId());
        assertEquals(volume, assignment.getVolume());
        assertEquals(location, assignment.getLocation());
        assertEquals(Assignment.Status.ASSIGNED, assignment.getStatus());
    }

    @Test
    void constructor_WithInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> new Assignment(null, volume, location));
        assertThrows(IllegalArgumentException.class, () -> new Assignment(orderId, null, location));
        assertThrows(IllegalArgumentException.class, () -> new Assignment(orderId, volume, null));
    }

    @Test
    void complete_WithNullLocation_ReturnsFailure() {
        Assignment assignment = createAssignment();

        UnitResult<Error> result = assignment.complete(null);
        assertTrue(result.isFailure());
        assertEquals("invalid_location", result.getError().getCode());
    }

    @Test
    void complete_WithLocationTooFar_ReturnsFailure() {
        Assignment assignment = createAssignment();
        Location farLocation = new Location(5, 5);

        UnitResult<Error> result = assignment.complete(farLocation);
        assertTrue(result.isFailure());
        assertEquals("invalid_location", result.getError().getCode());
    }

    @Test
    void complete_WithValidDiagonalNearbyLocation_ReturnsSuccess() {
        Assignment assignment = createAssignment();
        Location nearbyLocation = new Location(2, 4);

        UnitResult<Error> result = assignment.complete(nearbyLocation);

        assertTrue(result.isSuccess());
        assertEquals(Assignment.Status.COMPLETED, assignment.getStatus());
    }

    @Test
    void complete_WhenAlreadyCompleted_ReturnsFailure() {
        Assignment assignment = createAssignment();
        Location nearbyLocation = new Location(3, 4);

        UnitResult<Error> firstResult = assignment.complete(nearbyLocation);
        assertTrue(firstResult.isSuccess());
        assertEquals(Assignment.Status.COMPLETED, assignment.getStatus());

        UnitResult<Error> secondResult = assignment.complete(nearbyLocation);
        assertTrue(secondResult.isFailure());
        assertEquals("already_completed", secondResult.getError().getCode());
    }

    @Test
    void complete_WithSameLocation_ReturnsSuccess() {
        Assignment assignment = createAssignment();
        Location sameLocation = new Location(location.getX(), location.getY());

        UnitResult<Error> result = assignment.complete(sameLocation);
        assertTrue(result.isSuccess());
        assertEquals(Assignment.Status.COMPLETED, assignment.getStatus());
    }

    private Assignment createAssignment() {
        return new Assignment(orderId, volume, location);
    }

}
