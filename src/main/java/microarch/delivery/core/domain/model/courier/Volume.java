package microarch.delivery.core.domain.model.courier;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author maksimarts
 */
@Getter
@Embeddable
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Volume extends ValueObject<Volume> {

    @Column(name = "volume_value")
    private int value;

    public Volume(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Volume must be nonnegative");
        }
        this.value = value;
    }

    public Volume add(Volume other) {
        return new Volume(this.value + other.value);
    }

    public boolean isGreaterThan(Volume other) {
        return this.value > other.value;
    }

    public boolean isGreaterOrEqualsThan(Volume other) {
        return this.value >= other.value;
    }

    public boolean isLowerThan(Volume other) {
        return this.value < other.value;
    }

    public boolean isLowerOrEqualsThan(Volume other) {
        return this.value <= other.value;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(value);
    }
}
