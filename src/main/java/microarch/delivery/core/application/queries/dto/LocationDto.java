package microarch.delivery.core.application.queries.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import microarch.delivery.core.domain.model.shared_kernel.Location;

/**
 * @author maksimarts
 */
public record LocationDto(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y
) {
    public static LocationDto of(Location location) {
        return new LocationDto(location.getX(), location.getY());
    }
}
