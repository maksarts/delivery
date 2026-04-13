package microarch.delivery.core.application.commands;

import java.util.Objects;
import java.util.UUID;

import microarch.delivery.core.domain.model.shared_kernel.Location;

/**
 * @author maksimarts
 */
public record MoveCourierCommand(
        UUID courierId,
        Location location
) {
    public MoveCourierCommand {
        Objects.requireNonNull(courierId, "courierId is null");
        Objects.requireNonNull(location, "location is null");
    }
}
