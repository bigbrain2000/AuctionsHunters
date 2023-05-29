package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.model.enums.AuctionStatus;
import com.auctions.hunters.repository.AuctionRepository;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.auctions.hunters.model.enums.AuctionStatus.ACTIVE;
import static com.auctions.hunters.model.enums.CarStatus.AUCTIONED;
import static com.auctions.hunters.model.enums.CarStatus.NOT_AUCTIONED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class AuctionServiceTest {

    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private UserService userService;
    @Mock
    private CarService carService;

    private AuctionService uut;

    private User user;
    private Auction auction;
    private Car car;
    private Bid bid;

    private List<Auction> expectedAuctionList;
    @Mock
    private Page<Car> carPage;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new AuctionServiceImpl(auctionRepository, userService, carService));

        user = new User();
        user.setId(1);
        user.setUsername("Alex");

        auction = new Auction();
        auction.setId(1);
        auction.setCurrentPrice(110);
        auction.setUser(user);
        auction.setCar(car);
        auction.setStartTime(OffsetDateTime.now());
        auction.setEndTime(OffsetDateTime.now().plusDays(1));
        auction.setCurrentPrice(3800f);

        expectedAuctionList = List.of(auction);
        user.setAuctions(expectedAuctionList);

        car = new Car();
        car.setId(1);
        car.setStatus(NOT_AUCTIONED);

        bid = new Bid();
        bid.setId(1);
        bid.setAmount(3600);
        bid.setUser(user);
        bid.setAuction(auction);
    }

    @Test
    void save_validInput_returnsSuccess() {
        final float expectedAuctionPrice = 3500f;
        when(userService.getLoggedUsername()).thenReturn("Alex");
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(carService.updateCarAuctionStatus(car.getId(), AUCTIONED)).thenReturn(car);
        when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

        Auction actualAuction = uut.save(car, expectedAuctionPrice);

        assertNotNull(actualAuction);
        verify(userService, times(1)).getLoggedUsername();
        verify(userService, times(1)).findByUsername(anyString());
        verify(carService, times(1)).updateCarAuctionStatus(car.getId(), AUCTIONED);
        verify(auctionRepository, times(1)).save(any(Auction.class));
    }

    @Test
    void findAllActiveAuctions_foundActiveAuction_returnsSuccess() {
        auction.setStatus(ACTIVE);
        when(auctionRepository.findAll()).thenReturn(expectedAuctionList);

        List<Auction> actualActiveAuctionList = uut.findAllActiveAuctions();

        assertNotNull(actualActiveAuctionList);
        assertEquals(expectedAuctionList, actualActiveAuctionList);
        verify(auctionRepository, times(1)).findAll();
    }

    @Test
    void getAuctionByCarId_foundCarId_returnsSuccess() {
        when(auctionRepository.findByCarId(anyInt())).thenReturn(auction);

        Auction actualAuction = uut.getAuctionByCarId(1);

        assertNotNull(actualAuction);
        assertEquals(auction, actualAuction);
        verify(auctionRepository, times(1)).findByCarId(anyInt());
    }

    @Test
    void getAllAuctionsByUserId_userHasOngoingAuctions_returnsSuccess() {
        when(auctionRepository.findByUserId(anyInt())).thenReturn(expectedAuctionList);

        List<Auction> actualAuctionList = uut.getAllAuctionsByUserId(user.getId());

        assertNotNull(actualAuctionList);
        assertEquals(expectedAuctionList, actualAuctionList);
        verify(auctionRepository, times(1)).findByUserId(anyInt());
    }

    @Test
    void getTopBidAuctions_oneActiveAuctionFound_returnsSuccess() {
        auction.setStatus(ACTIVE);
        when(auctionRepository.findAll()).thenReturn(expectedAuctionList);

        List<Auction> actualAuctionList = uut.getTopBidAuctions(1);

        assertNotNull(actualAuctionList);
        assertEquals(expectedAuctionList, actualAuctionList);
        verify(auctionRepository, times(1)).findAll();
    }

    @Test
    void getBidderIds_biddersFoundAtAuction_returnsSuccess() {
        List<Integer> expectedBidderIds = Collections.singletonList(bid.getId());
        auction.setBidders(Collections.singletonList(bid));
        when(auctionRepository.findById(anyInt())).thenReturn(Optional.ofNullable(auction));

        List<Integer> actualBidderIds = uut.getBidderIds(auction.getId());

        assertNotNull(actualBidderIds);
        assertEquals(expectedBidderIds, actualBidderIds);
        verify(auctionRepository, times(1)).findById(anyInt());

    }

    @Test
    void setCurrentPriceForEachCarPage() {
        List<Float> actualCarPage = uut.setCurrentPriceForEachCarPage(carPage);

        assertNotNull(actualCarPage);
    }

    @Test
    void updateAuctionCurrentPrice_foundAuction_returnsSuccess() {
        when(auctionRepository.findById(anyInt())).thenReturn(Optional.of(auction));
        when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

        uut.updateAuctionCurrentPrice(auction.getId(), 3700, user.getId());

        verify(auctionRepository, times(1)).findById(anyInt());
        verify(auctionRepository, times(1)).save(any(Auction.class));
    }

    @Test
    void manageFinishedAuctions() {
        when(auctionRepository.findAll()).thenReturn(expectedAuctionList);

        Page<Car> result = uut.manageFinishedAuctions(user, carPage);

        assertSame(carPage, result);
        verify(auctionRepository, times(1)).findAll();
    }

    @Test
    void updateFinishedAuctionsStatusAsSold() {
        when(userService.getLoggedUsername()).thenReturn(user.getUsername());
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(auctionRepository.findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class)))
                .thenReturn(expectedAuctionList);

        uut.updateFinishedAuctionsStatusAsSold();

        verify(userService, times(1)).getLoggedUsername();
        verify(userService, times(1)).findByUsername(anyString());
        verify(auctionRepository, times(1)).findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class));
    }

    @Test
    void getCarsFromFinishedAuctionsForBuyerId() {
        auction.setCar(car);
        when(userService.getLoggedUsername()).thenReturn(user.getUsername());
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(auctionRepository.findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class)))
                .thenReturn(expectedAuctionList);
        when(carService.findAllByIdIn(Collections.singletonList(car.getId()))).thenReturn(Collections.singletonList(car));

        List<Car> actualCarList = uut.getCarsFromFinishedAuctionsForBuyerId();

        assertNotNull(actualCarList);
        assertEquals(Collections.singletonList(car), actualCarList);
        verify(userService, times(1)).getLoggedUsername();
        verify(userService, times(1)).findByUsername(anyString());
        verify(auctionRepository, times(1)).findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class));
        verify(carService, times(1)).findAllByIdIn(any(List.class));
    }

    @Test
    void getFinishedAuctionsCurrentPrice() {
        when(userService.getLoggedUsername()).thenReturn(user.getUsername());
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(auctionRepository.findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class)))
                .thenReturn(expectedAuctionList);

        List<Float> finishedAuctionsCurrentPrice = uut.getFinishedAuctionsCurrentPrice();

        assertNotNull(finishedAuctionsCurrentPrice);
        verify(userService, times(1)).getLoggedUsername();
        verify(userService, times(1)).findByUsername(anyString());
        verify(auctionRepository, times(1)).findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class));
    }

    @Test
    void getTotalPriceToPay_finishedAuctionListRetrieved_returnsSuccess() {
        when(userService.getLoggedUsername()).thenReturn(user.getUsername());
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(auctionRepository.findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class)))
                .thenReturn(expectedAuctionList);

        Float totalPriceToPay = uut.getTotalPriceToPay();

        assertNotNull(totalPriceToPay);
        assertEquals(auction.getCurrentPrice(), totalPriceToPay);
        verify(userService, times(1)).getLoggedUsername();
        verify(userService, times(1)).findByUsername(anyString());
        verify(auctionRepository, times(1)).findAllByBuyerIdAndStatus(anyInt(), any(AuctionStatus.class));
    }

    @Test
    void findAuctionsByCars_foundCars_ReturnsSuccess() {
        when(auctionRepository.findByCarIn(any(List.class))).thenReturn(expectedAuctionList);

        List<Auction> actualAuctionList = uut.findAuctionsByCars(Collections.singletonList(car));

        assertNotNull(actualAuctionList);
        assertEquals(expectedAuctionList, actualAuctionList);
        verify(auctionRepository, times(1)).findByCarIn(any(List.class));
    }
}