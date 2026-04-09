package microarch.delivery.core.application.commands;

import java.util.Objects;

/**
 * @author maksimarts
 */
public record CreateCourierCommand(String name) {
    public CreateCourierCommand {
        Objects.requireNonNull(name, "name is null");
    }
}
