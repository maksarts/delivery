package microarch.delivery.core.application.queries.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import microarch.delivery.core.domain.model.order.Order;

/**
 * @author maksimarts
 */
public record OrderDto(
        @JsonProperty("id") UUID id,
        @JsonProperty("location") LocationDto location
) {
    public static OrderDto of(Order o) {
        return new OrderDto(
                o.getId(),
                LocationDto.of(o.getLocation())
        );
    }
}
