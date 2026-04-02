package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author maksimarts
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourierRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CourierRepository courierRepository;

    @Test
    void saveCourier() {
        Courier courier = Courier.createNew("Vasya", new Location(2, 2));

        courierRepository.save(courier);

        Optional<Courier> foundO = courierRepository.findById(courier.getId());
        assertTrue(foundO.isPresent());

        Courier found = foundO.get();
        assertEquals(courier.getId(), found.getId());
        assertEquals(courier.getName(), found.getName());
        assertEquals(courier.getCurrLocation(), found.getCurrLocation());

        found.moveTo(new Location(5, 5));
        courierRepository.save(found);

        foundO = courierRepository.findById(courier.getId());
        assertTrue(foundO.isPresent());
        assertEquals(found.getId(), foundO.get().getId());
        assertEquals(found.getName(), foundO.get().getName());
        assertEquals(found.getCurrLocation(), foundO.get().getCurrLocation());
    }

    @Test
    void findAllCouriers() {
        Courier courier1 = Courier.createNew("Vasya", new Location(2, 2));
        Courier courier2 = Courier.createNew("Petya", new Location(3, 3));

        courierRepository.save(courier1);
        courierRepository.save(courier2);

        List<Courier> allCouriers = courierRepository.findAll();

        assertEquals(2, allCouriers.size());
        assertTrue(allCouriers.stream().anyMatch(c -> c.getId().equals(courier1.getId())));
        assertTrue(allCouriers.stream().anyMatch(c -> c.getId().equals(courier2.getId())));
    }
}
