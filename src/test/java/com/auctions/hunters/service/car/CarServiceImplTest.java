package com.auctions.hunters.service.car;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.service.car.vincario.VinDecoderService;
import com.auctions.hunters.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.auctions.hunters.model.enums.CarStatus.AUCTIONED;
import static com.auctions.hunters.model.enums.CarStatus.NOT_AUCTIONED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private UserService userService;
    @Mock
    private VinDecoderService vinDecoderService;

    private CarService uut;

    private Car car;
    private final String VALID_VIN = "WBAEY31090KS46562";

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new CarServiceImpl(carRepository, userService, vinDecoderService));

        car = new Car();
        car.setId(1);
        car.setStatus(NOT_AUCTIONED);
        car.setBody("Sedan/Saloon");
    }

    @Test
    void save_newCar_returnsSuccess() throws CarPayloadFailedToCreateException, NotEnoughLookupsException, UnrecognizedVinException, CarVinAlreadyExistsException {
        when(carRepository.findCarByVin(anyString())).thenReturn(Optional.empty());
        when(vinDecoderService.decodeVin(anyString())).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car actualCar = uut.save(VALID_VIN);

        assertNotNull(actualCar);
        assertEquals(car, actualCar);
        verify(carRepository, times(1)).findCarByVin(anyString());
        verify(vinDecoderService, times(1)).decodeVin(anyString());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void save_alreadyExistingCar_throwsException() {
        when(carRepository.findCarByVin(anyString())).thenReturn(Optional.of(car));

        assertThrows(CarVinAlreadyExistsException.class, () -> uut.save(VALID_VIN));

        verify(carRepository, times(1)).findCarByVin(anyString());
    }

    @Test
    void deleteById_carNotAuctioned_returnsSuccess() throws CarExistsInAuctionException {
        when(carRepository.findById(anyInt())).thenReturn(Optional.of(car));
        doNothing().when(carRepository).delete(car);

        uut.deleteById(1);

        verify(carRepository, times(1)).findById(anyInt());
    }

    @Test
    void deleteById_carIsNotFound_throwsException() {
        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> uut.deleteById(1));

        verify(carRepository, times(1)).findById(anyInt());
    }

    @Test
    void findAll_carListNotEmpty_returnsPopulatedList() {
        when(carRepository.findAll()).thenReturn(Collections.singletonList(car));

        List<Car> actualList = uut.findAll();

        assertNotNull(actualList);
        assertFalse(actualList.isEmpty());
        assertEquals(car, actualList.get(0));
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void findAll_carListIsEmpty_returnsEmptyList() {
        when(carRepository.findAll()).thenReturn(Collections.emptyList());

        List<Car> actualList = uut.findAll();

        assertNotNull(actualList);
        assertTrue(actualList.isEmpty());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void getCarById_existingCar_returnsSuccess() {
        when(carRepository.findById(anyInt())).thenReturn(Optional.of(car));

        Car actualCar = uut.getCarById(1);

        assertNotNull(actualCar);
        assertEquals(car, actualCar);
        verify(carRepository, times(1)).findById(anyInt());
    }

    @Test
    void getCarById_notExistingCar_throwsException() {
        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> uut.getCarById(1));

        verify(carRepository, times(1)).findById(anyInt());
    }

    @Test
    void updateCarAuctionStatus_foundCar_returnsSuccess() {
        when(carRepository.findById(anyInt())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        Car actualCar = uut.updateCarAuctionStatus(1, AUCTIONED);

        assertNotNull(actualCar);
        assertEquals(car, actualCar);
        verify(carRepository, times(1)).findById(anyInt());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void updateCarAuctionStatus_notFoundCar_throwsException() {
        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> uut.updateCarAuctionStatus(1, AUCTIONED));

        verify(carRepository, times(1)).findById(anyInt());
    }

    @Test
    void getAuthenticatedUserCarsList_foundUserAndCarList_returnsSuccess() {
        User user = new User();
        user.setId(1);
        user.setUsername("Alex");
        user.setPassword("password");
        user.setCarList(Collections.singletonList(car));
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(userService.getLoggedUsername()).thenReturn(user.getUsername());
        when(carRepository.findByUserId(anyInt())).thenReturn(Collections.singletonList(car));

        List<Car> authenticatedUserCarsList = uut.getAuthenticatedUserCarsList();

        assertNotNull(authenticatedUserCarsList);
        assertFalse(authenticatedUserCarsList.isEmpty());
        assertEquals(car, authenticatedUserCarsList.get(0));
        verify(userService, times(1)).findByUsername(anyString());
        verify(userService, times(1)).getLoggedUsername();
        verify(carRepository, times(1)).findByUserId(anyInt());
    }

    @Test
    void findAllByIdIn_foundCarList_returnsSuccess() {
        when(carRepository.findAllByIdIn(anyList())).thenReturn(Collections.singletonList(car));

        List<Car> actualCarList = uut.findAllByIdIn(List.of(1));

        assertNotNull(actualCarList);
        assertFalse(actualCarList.isEmpty());
        assertEquals(car, actualCarList.get(0));
        verify(carRepository, times(1)).findAllByIdIn(anyList());
    }

    @Test
    void getAllCarsByBodyType_foundCarByBodyType_returnsSuccess() {
        final String bodyType = "Sedan/Saloon";
        when(carRepository.findByBody(anyString())).thenReturn(Collections.singletonList(car));

        List<Car> actualCarList = uut.getAllCarsByBodyType(bodyType);

        assertNotNull(actualCarList);
        assertFalse(actualCarList.isEmpty());
        assertEquals(car, actualCarList.get(0));
        verify(carRepository, times(1)).findByBody(anyString());
    }
}