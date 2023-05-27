package com.auctions.hunters.service.car;

import com.auctions.hunters.config.VincarioProperties;
import com.auctions.hunters.exceptions.UnrecognizedVinException;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.car.coverter.CarPayloadConverter;
import com.auctions.hunters.service.car.vincario.VinDecoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class VinDecoderServiceTest {

    @Mock
    private VincarioProperties properties;

    @Mock
    private CarPayloadConverter converter;

    @InjectMocks
    private VinDecoderService uut;

    private Car car;
    private final String VALID_VIN = "WBAEY31090KS46562";

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new VinDecoderService(properties, converter));

        car = new Car();
    }

    @Test
    void isVinValid_validVin_returnsSuccess() throws UnrecognizedVinException {
        assertTrue(uut.isVinValid(VALID_VIN));
    }

    @Test
    void isVinValid_invalidVin_throwsException() {
        assertThrows(UnrecognizedVinException.class, () -> uut.isVinValid(VALID_VIN + "aa"));
    }
}