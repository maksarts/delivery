package microarch.delivery.core.domain.model.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import microarch.delivery.core.domain.model.courier.Assignment;

/**
 * @author maksimarts
 */
public enum OrderStatus {
    CREATED, ASSIGNED, COMPLETED;

    @JsonCreator
    public static Assignment.Status fromValue(String value) {
        return Assignment.Status.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
