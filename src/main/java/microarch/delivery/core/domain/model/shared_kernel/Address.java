package microarch.delivery.core.domain.model.shared_kernel;

import java.util.List;
import java.util.Objects;

import libs.ddd.ValueObject;
import lombok.Getter;

/**
 * @author maksimarts
 */
@Getter
public class Address extends ValueObject<Address> {
    private final String country;
    private final String city;
    private final String street;
    private final String house;
    private final String apartment;

    public Address(
            String country,
            String city,
            String street,
            String house,
            String apartment
    ) {
        Objects.requireNonNull(country, "Country is required");
        Objects.requireNonNull(city, "City is required");
        Objects.requireNonNull(street, "Street is required");
        Objects.requireNonNull(house, "House is required");
        Objects.requireNonNull(apartment, "Apartment is required");
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(country, city, street, house, apartment);
    }
}
