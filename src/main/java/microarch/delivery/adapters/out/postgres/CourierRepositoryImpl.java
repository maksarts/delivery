package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Repository;

/**
 * @author maksimarts
 */
@Repository
@RequiredArgsConstructor
public class CourierRepositoryImpl implements CourierRepository {

    private final JpaCourierRepository jpaCourierRepository;

    @Override
    public void save(Courier courier) {
        jpaCourierRepository.save(courier);
    }

    @Override
    public Optional<Courier> findById(UUID id) {
        return jpaCourierRepository.findById(id);
    }

    @Override
    public List<Courier> findAll() {
        return jpaCourierRepository.findAll();
    }
}
