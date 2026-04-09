package microarch.delivery.core.application.commands;

import java.util.Objects;
import java.util.UUID;

/**
 * @author maksimarts
 */
public record CompleteOrderCommand(UUID courierId, UUID orderId) {
    public CompleteOrderCommand {
        Objects.requireNonNull(courierId, "courierId is null");
        Objects.requireNonNull(orderId, "orderId is null");
    }
}
