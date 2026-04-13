package microarch.delivery.core.application.commands;

import java.util.Objects;
import java.util.UUID;

import microarch.delivery.core.domain.model.courier.Volume;
import microarch.delivery.core.domain.model.shared_kernel.Address;

/**
 * @author maksimarts
 */
public record CreateOrderCommand(UUID orderId, Address address, Volume volume) {
    public CreateOrderCommand {
        Objects.requireNonNull(orderId, "orderId is null");
        Objects.requireNonNull(address, "address is null");
        Objects.requireNonNull(volume, "volume is null");
    }
}
