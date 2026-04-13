package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;

/**
 * @author maksimarts
 */
public interface CreateCourierCommandHandler {
    UnitResult<Error> handle(CreateCourierCommand command);
}
