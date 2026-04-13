package microarch.delivery.core.application.queries;

import java.util.List;

import microarch.delivery.core.application.queries.dto.CourierDto;

/**
 * @author maksimarts
 */
public record GetAllCouriersResponse(List<CourierDto> items) {
}
