package microarch.delivery.core.application.commands;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.shared_kernel.Location;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author maksimarts
 */
class MoveCourierCommandHandlerImplTest {
    private final UUID courierId = UUID.randomUUID();
    private final Location location = new Location(5, 5);
    private CourierRepository courierRepository;
    private MoveCourierCommandHandlerImpl handler;

    @BeforeEach
    void setUp() {
        courierRepository = mock(CourierRepository.class);
        handler = new MoveCourierCommandHandlerImpl(courierRepository);
    }

    @Test
    void shouldThrowWhenCourierNotFound() {
        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> handler.handle(new MoveCourierCommand(courierId, location))
        );
    }

    @Test
    void shouldReturnFailureWhenMoveToFails() {
        Courier courier = mock(Courier.class);
        Error error = Error.of("move_error", "Cannot move courier");

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(courier.moveTo(location)).thenReturn(UnitResult.failure(error));

        UnitResult<Error> result = handler.handle(new MoveCourierCommand(courierId, location));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(error);
    }

    @Test
    void shouldNotSaveCourierWhenMoveToFails() {
        Courier courier = mock(Courier.class);
        Error error = Error.of("move_error", "Cannot move courier");

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(courier.moveTo(location)).thenReturn(UnitResult.failure(error));

        handler.handle(new MoveCourierCommand(courierId, location));

        verify(courierRepository, never()).save(any());
    }

    @Test
    void shouldSaveCourierOnSuccess() {
        Courier courier = mock(Courier.class);

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(courier.moveTo(location)).thenReturn(UnitResult.success());

        handler.handle(new MoveCourierCommand(courierId, location));

        verify(courierRepository).save(courier);
    }

    @Test
    void shouldReturnSuccessOnSuccess() {
        Courier courier = mock(Courier.class);

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(courier.moveTo(location)).thenReturn(UnitResult.success());

        UnitResult<Error> result = handler.handle(new MoveCourierCommand(courierId, location));

        assertThat(result.isSuccess()).isTrue();
    }
}
