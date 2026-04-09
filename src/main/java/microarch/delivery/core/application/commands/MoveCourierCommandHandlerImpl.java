package microarch.delivery.core.application.commands;

import java.util.NoSuchElementException;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

/**
 * @author maksimarts
 */
@Service
@RequiredArgsConstructor
public class MoveCourierCommandHandlerImpl implements MoveCourierCommandHandler {

    private final CourierRepository courierRepository;

    @Override
    public UnitResult<Error> handle(MoveCourierCommand command) {
        Courier courier = courierRepository.findById(command.courierId())
                .orElseThrow(() -> new NoSuchElementException("Courier not found"));

        UnitResult<Error> result = courier.moveTo(command.location());
        if (result.isFailure()) {
            return result;
        }

        courierRepository.save(courier);
        return UnitResult.success();
    }
}
