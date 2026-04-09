package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;

/**
 * @author maksimarts
 */
public interface CreateOrderCommandHandler {
    UnitResult<Error> handle(CreateOrderCommand command);
}
