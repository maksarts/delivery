package microarch.delivery.core.domain.model.shared_kernel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author maksimarts
 */
class LocationTest {

    @Test
    void constructor_ShouldCreateLocation_WhenValidCoordinates() {
        Location location = new Location(5, 5);
        assertNotNull(location);
    }

    @Test
    void constructor_ShouldThrowException_WhenInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new Location(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Location(5, 0));
        assertThrows(IllegalArgumentException.class, () -> new Location(11, 5));
        assertThrows(IllegalArgumentException.class, () -> new Location(5, 11));
    }

    @Test
    void distanceTo_ShouldCalculateManhattanDistance_notEquals() {
        Location location1 = new Location(1, 1);
        Location location2 = new Location(4, 5);
        assertEquals(7, location1.distanceTo(location2));
    }

    @Test
    void distanceTo_ShouldCalculateManhattanDistance_Equals() {
        Location location1 = new Location(3, 3);
        Location location2 = new Location(3, 3);
        assertEquals(0, location1.distanceTo(location2));
    }

    @Test
    void equals() {
        Location location1 = new Location(3, 3);
        Location location2 = new Location(3, 3);
        assertEquals(location1, location2);
    }

    @Test
    void chebyshevDistanceTo_shouldCalculateCorrectDistance() {
        Location location1 = new Location(1, 1);
        Location location2 = new Location(4, 5);

        int distance = location1.chebyshevDistanceTo(location2);

        assertEquals(4, distance);
    }

    @Test
    void chebyshevDistanceTo_shouldReturnZeroForSameLocation() {
        Location location1 = new Location(3, 7);
        Location location2 = new Location(3, 7);

        int distance = location1.chebyshevDistanceTo(location2);

        assertEquals(0, distance);
    }

    @Test
    void chebyshevDistanceTo_shouldWorkWithNegativeDifferences() {
        Location location1 = new Location(5, 8);
        Location location2 = new Location(2, 3);

        int distance = location1.chebyshevDistanceTo(location2);

        assertEquals(5, distance);
    }
}
