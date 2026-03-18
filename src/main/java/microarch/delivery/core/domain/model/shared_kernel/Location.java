package microarch.delivery.core.domain.model.shared_kernel;

import java.util.List;

import libs.ddd.ValueObject;
import lombok.Getter;

/**
 * @author maksimarts
 */
@Getter
public class Location extends ValueObject<Location> {

    public static final int MIN_COORDINATE = 1;
    public static final int MAX_COORDINATE = 10;

    private final int x;
    private final int y;

    public Location(int x, int y) {
        if (x < MIN_COORDINATE || y < MIN_COORDINATE) {
            throw new IllegalArgumentException("Location coordinates must be greater than " + MIN_COORDINATE);
        }
        if (x > MAX_COORDINATE || y > MAX_COORDINATE) {
            throw new IllegalArgumentException("Location coordinates must be less than " + MAX_COORDINATE);
        }
        this.x = x;
        this.y = y;
    }

    public int distanceTo(Location location) {
        return Math.abs(x - location.x) + Math.abs(y - location.y);
    }

    public int chebyshevDistanceTo(Location location) {
        return Math.max(Math.abs(this.x - location.x), Math.abs(this.y - location.y));
    }


    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }
}
