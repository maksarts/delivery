package microarch.delivery.core.application.queries.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import microarch.delivery.core.domain.model.courier.Courier;

/**
 * @author maksimarts
 */
public record CourierDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("location") LocationDto location
) {
    public static CourierDto of(Courier courier) {
        return new CourierDto(
                courier.getId(),
                courier.getName(),
                LocationDto.of(courier.getCurrLocation())
        );
    }
}
