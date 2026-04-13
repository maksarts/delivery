package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;

/**
 * @author maksimarts
 */
public interface MoveCourierCommandHandler {
    UnitResult<Error> handle(MoveCourierCommand command);
}
