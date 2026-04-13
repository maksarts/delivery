package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

/**
 * @author maksimarts
 */
@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository courierRepository;

    @Override
    public UnitResult<Error> handle(CreateCourierCommand command) {
        Courier courier = Courier.createNew(command.name(), new Location(1, 1));
        courierRepository.save(courier);
        return UnitResult.success();
    }
}
