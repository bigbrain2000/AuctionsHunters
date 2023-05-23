package com.auctions.hunters.controller;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.ml.RecommendationServiceImpl;
import com.auctions.hunters.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionControllerTest {

    @Mock
    private CarService carService;
    @Mock
    private AuctionService auctionService;
    @Mock
    private UserService userService;
    @Mock
    private RecommendationServiceImpl recommendationService;
    @Mock
    private Model modelAtr;
    @Mock
    private Car car;
    @Mock
    private Auction auction;

    @InjectMocks
    private AuctionController uut;

    private final int page = 0;

    @Test
    void getAuction_notEmptyCarList_returnsPopulatedCarList() {
        when(carService.getCarById(anyInt())).thenReturn(car);

        String result = uut.getAuction(1, modelAtr);

        verify(carService, times(1)).getCarById(anyInt());
        assertEquals("/auction", result);
        verify(this.modelAtr, times(1)).addAttribute(eq("car"), any(Car.class));
    }

    @Test
    void createAuction_validData_throwsException() {
        final String minimumPrice = "22";
        when(carService.getCarById(anyInt())).thenReturn(car);
        when(auctionService.save(car, 22)).thenReturn(auction);

        String result = uut.createAuction(1, minimumPrice, modelAtr);

        assertEquals("redirect:/", result);
        verify(carService, times(1)).getCarById(anyInt());
        verify(this.modelAtr, times(1)).addAttribute(eq("car"), any(Car.class));
    }

    @Test
    void createAuction_invalidData_createsNewAuction() {
        final String minimumPrice = "22a";
        when(carService.getCarById(anyInt())).thenReturn(car);

        String result = uut.createAuction(1, minimumPrice, modelAtr);

        assertEquals("/login", result);
        verify(carService, times(1)).getCarById(anyInt());
        verify(this.modelAtr, times(1)).addAttribute(eq("car"), any(Car.class));
    }

    @Test
    void getAuctions() {
    }

    @Test
    void getRecommendedAuctions() {
    }

    @Test
    void getAuctionsWithSuvCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("SUV")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithSuvCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("SUV");
    }

    @Test
    void getAuctionsWithSuvCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("SUV")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithSuvCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("SUV");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithSedanCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Sedan/Saloon")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithSedanCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Sedan/Saloon");
    }

    @Test
    void getAuctionsWithSedanCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Sedan/Saloon")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithSedanCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Sedan/Saloon");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithPickupCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Pickup")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithPickupCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Pickup");
    }

    @Test
    void getAuctionsWithPickupCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Pickup")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithPickupCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Pickup");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithCoupeCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Coupe")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithCoupeCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Coupe");
    }

    @Test
    void getAuctionsWithCoupeCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Coupe")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithCoupeCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Coupe");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithConvertibleCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Convertible")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithConvertibleCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Convertible");
    }

    @Test
    void getAuctionsWithConvertibleCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Convertible")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithConvertibleCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Convertible");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithRoadsterCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Roadster")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithRoadsterCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Roadster");
    }

    @Test
    void getAuctionsWithRoadsterCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Roadster")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithRoadsterCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Roadster");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithHatchbackCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Hatchback")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithHatchbackCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Hatchback");
    }

    @Test
    void getAuctionsWithHatchbackCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Hatchback")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithHatchbackCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Hatchback");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithMinivanCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Minivan")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithMinivanCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Minivan");
    }

    @Test
    void getAuctionsWithMinivanCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Minivan")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithMinivanCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Minivan");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }

    @Test
    void getAuctionsWithWagonCars_emptyCarList_returnsEmpty() {
        when(carService.getAllCarsByBodyType("Wagon")).thenReturn(new ArrayList<>());

        String result = uut.getAuctionsWithWagonCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/no_car_for_category", result);
        verify(carService, times(1)).getAllCarsByBodyType("Wagon");
    }

    @Test
    void getAuctionsWithWagonCars_notEmptyCarList_returnsPopulatedList() {
        List<Car> allSuvCars = new ArrayList<>();
        allSuvCars.add(new Car());
        when(carService.getAllCarsByBodyType("Wagon")).thenReturn(allSuvCars);

        Page<Car> carPage = new PageImpl<>(allSuvCars, PageRequest.of(page, 10), allSuvCars.size());
        when(carService.getCarPage(anyInt(), any())).thenReturn(carPage);

        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        auctionsMinimumPriceList.add(1000f);
        when(auctionService.setCurrentPriceForEachCarPage(any())).thenReturn(auctionsMinimumPriceList);

        String result = uut.getAuctionsWithWagonCars(page, null, null, null, null, null, null, this.modelAtr);

        assertEquals("/auction_list", result);
        verify(carService, times(1)).getAllCarsByBodyType("Wagon");
        verify(carService, times(1)).getCarPage(anyInt(), any());
        verify(auctionService, times(1)).setCurrentPriceForEachCarPage(any());
        verify(this.modelAtr, times(1)).addAttribute(eq("carPage"), any(Page.class));
        verify(this.modelAtr, times(1)).addAttribute("currentPage", page);
        verify(this.modelAtr, times(1)).addAttribute(eq("pageNumbers"), anyList());
        verify(this.modelAtr, times(1)).addAttribute("auctionsMinimumPriceList", auctionsMinimumPriceList);
        verify(this.modelAtr, times(1)).addAttribute(eq("allActiveAuctionsList"), anyList());
    }
}