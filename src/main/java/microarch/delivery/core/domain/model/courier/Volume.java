package microarch.delivery.core.domain.model.courier;

import java.util.List;

import libs.ddd.ValueObject;

/**
 * @author maksimarts
 */
public class Volume extends ValueObject<Volume> {

    private final int value;

    public Volume(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Volume must be positive");
        }
        this.value = value;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(value);
    }
}
