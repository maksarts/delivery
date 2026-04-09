package microarch.delivery.core.application.commands;

import java.util.Objects;
import java.util.UUID;

import lombok.Getter;
import microarch.delivery.core.domain.model.courier.Volume;

/**
 * @author maksimarts
 */
@Getter
public class CreateOrderCommand {

    private final UUID orderId;
    private final String country;
    private final String city;
    private final String street;
    private final String house;
    private final String apartment;
    private final Volume volume;

    public CreateOrderCommand(
            UUID orderId,
            String country,
            String city,
            String street,
            String house,
            String apartment,
            int volume
    ) {
        Objects.requireNonNull(orderId, "orderId is null");
        Objects.requireNonNull(country, "country is null");
        Objects.requireNonNull(city, "city is null");
        Objects.requireNonNull(street, "street is null");
        Objects.requireNonNull(house, "house is null");
        Objects.requireNonNull(apartment, "apartment is null");
        this.orderId = orderId;
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
        this.volume = new Volume(volume);
    }
}
