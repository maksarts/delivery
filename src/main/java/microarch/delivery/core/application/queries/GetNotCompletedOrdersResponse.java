package microarch.delivery.core.application.queries;

import java.util.List;

import microarch.delivery.core.application.queries.dto.OrderDto;

/**
 * @author maksimarts
 */
public record GetNotCompletedOrdersResponse(List<OrderDto> items) {
}
