package microarch.delivery.core.application.queries;

import java.util.List;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.queries.dto.CourierDto;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

/**
 * @author maksimarts
 */
@Service
@RequiredArgsConstructor
public class GetAllCouriersQueryHandlerImpl implements GetAllCouriersQueryHandler {

    private final CourierRepository courierRepository;

    @Override
    public Result<GetAllCouriersResponse, Error> handle(GetAllCouriersQuery query) {
        List<CourierDto> couriers = courierRepository.findAll()
                .stream()
                .map(CourierDto::of)
                .toList();
        return Result.success(new GetAllCouriersResponse(couriers));
    }
}
