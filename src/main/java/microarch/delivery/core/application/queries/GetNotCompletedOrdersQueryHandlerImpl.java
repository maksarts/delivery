package microarch.delivery.core.application.queries;

import java.util.List;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.queries.dto.OrderDto;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * @author maksimarts
 */
@Service
@RequiredArgsConstructor
public class GetNotCompletedOrdersQueryHandlerImpl implements GetNotCompletedOrdersQueryHandler {

    private final OrderRepository orderRepository;

    @Override
    public Result<GetNotCompletedOrdersResponse, Error> handle(GetNotCompletedOrdersQuery query) {
        List<OrderDto> orders = orderRepository.findAllNotCompleted()
                .stream()
                .map(OrderDto::of)
                .toList();
        return Result.success(new GetNotCompletedOrdersResponse(orders));
    }
}
