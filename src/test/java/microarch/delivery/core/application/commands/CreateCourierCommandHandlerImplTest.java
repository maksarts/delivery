package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author maksimarts
 */
class CreateCourierCommandHandlerImplTest {
    private CourierRepository courierRepository;
    private CreateCourierCommandHandlerImpl handler;

    @BeforeEach
    void setUp() {
        courierRepository = mock(CourierRepository.class);
        handler = new CreateCourierCommandHandlerImpl(courierRepository);
    }

    @Test
    void shouldSaveCourierOnHandle() {
        handler.handle(new CreateCourierCommand("John"));

        verify(courierRepository).save(any(Courier.class));
    }

    @Test
    void shouldReturnSuccessOnHandle() {
        UnitResult<Error> result = handler.handle(new CreateCourierCommand("John"));

        assertThat(result.isSuccess()).isTrue();
    }
}
