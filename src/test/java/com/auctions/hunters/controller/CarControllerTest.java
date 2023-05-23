package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.bid.BidService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.image.ImageService;
import com.auctions.hunters.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @Mock
    private CarService carService;
    @Mock
    private ImageService imageService;
    @Mock
    private AuctionService auctionService;
    @Mock
    private UserService userService;
    @Mock
    private BidService bidService;

    @InjectMocks
    private CarController uut;

    @Mock
    private Car car;
    @Mock
    private Model modelAtr;
    @Mock
    private Auction auction;
    @Mock
    private Bid bid;
    private final String VALID_VIN = "WBAEY31090KS46562";

    @Test
    void getCar_validCarInput_returnsSuccess() {
        assertEquals("/add_car", uut.getCar(car));
    }

    @Test
    void addCar_validVin_returnsSuccess() throws CarPayloadFailedToCreateException, NotEnoughLookupsException, UnrecognizedVinException, CarVinAlreadyExistsException {
        when(carService.save(anyString())).thenReturn(car);

        String result = uut.addCar(VALID_VIN);

        assertEquals("redirect:/images/upload", result);
        verify(carService, times(1)).save(anyString());
    }

    @Test
    void deleteCar_carIsNotAuctioned_returnsSuccess() throws CarExistsInAuctionException {
        car.setId(1);
        doNothing().when(carService).deleteById(anyInt());

        String result = uut.deleteCar(1);

        assertEquals("redirect:/cars", result);
        verify(carService, times(1)).deleteById(anyInt());
    }

    @Test
    void getCarById_foundCarById_returnsSuccess() {
        car.setId(1);
        when(carService.getCarById(anyInt())).thenReturn(car);
        when(imageService.findAllImagesByCarId(anyInt())).thenReturn(Collections.emptyList());

        String result = uut.getCarById(1, modelAtr);

        assertEquals("/view_car", result);
        verify(carService, times(1)).getCarById(anyInt());
        verify(imageService, times(1)).findAllImagesByCarId(anyInt());
    }

    @Test
    void getAuctionedCar_foundCarById_returnsSuccess() {
        car.setId(1);
        when(carService.getCarById(anyInt())).thenReturn(car);
        when(imageService.findAllImagesByCarId(anyInt())).thenReturn(Collections.emptyList());

        String result = uut.getAuctionedCar(1, modelAtr);

        assertEquals("/view_auctioned_car", result);
        verify(carService, times(1)).getCarById(anyInt());
        verify(imageService, times(1)).findAllImagesByCarId(anyInt());
    }

    @Test
    void createNewBid_validBid_returnsSuccess() {
        car.setId(1);
        auction.setCurrentPrice(10);
        when(auctionService.getAuctionByCarId(anyInt())).thenReturn(auction);

        String result = uut.createNewBid(1, 11);

        assertEquals("redirect:/auctions", result);
        verify(auctionService, times(1)).getAuctionByCarId(anyInt());
    }

    @Test
    void createNewBid_invalidBid_throwsException() throws LowBidAmountException {
        car.setId(1);
        auction.setCurrentPrice(10);
        when(auctionService.getAuctionByCarId(anyInt())).thenReturn(auction);
        when(bidService.save(5, auction)).thenThrow(LowBidAmountException.class);

        String result = uut.createNewBid(1, 5);

        assertEquals("redirect:/bid/car/{carId}", result);
        verify(auctionService, times(1)).getAuctionByCarId(anyInt());
        verify(bidService, times(1)).save(5, auction);
    }
}