package com.auctions.hunters.service.ml;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.auction.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static com.auctions.hunters.model.enums.CarStatus.NOT_AUCTIONED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private AuctionService auctionService;

    private RecommendationService uut;

    private User user;
    private Car car;
    private List<Auction> expectedAuctionList;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new RecommendationServiceImpl(auctionService));

        user = new User();
        user.setId(1);
        user.setUsername("Alex");

        Auction auction = new Auction();
        auction.setId(1);
        auction.setCurrentPrice(110);
        auction.setUser(user);
        auction.setCar(car);
        auction.setStartTime(OffsetDateTime.now());
        auction.setEndTime(OffsetDateTime.now().plusDays(1));
        auction.setCurrentPrice(3800f);

        car = new Car();
        car.setId(1);
        car.setStatus(NOT_AUCTIONED);

        expectedAuctionList = List.of(auction);
        user.setAuctions(expectedAuctionList);
    }

    @Test
    void getRecommendedAuctionedCarsForUser_returnsSuccess() {
        when(auctionService.getAllAuctionsByUserId(anyInt())).thenReturn(expectedAuctionList);
        when(auctionService.getBidderIds(anyInt())).thenReturn(Collections.singletonList(user.getId()));
        when(auctionService.getTopBidAuctions(anyInt())).thenReturn(expectedAuctionList);

        List<Car> recommendedAuctionedCarsForUser = uut.getRecommendedAuctionedCarsForUser(user);

        assertNotNull(recommendedAuctionedCarsForUser);
        verify(auctionService, times(1)).getAllAuctionsByUserId(anyInt());
        verify(auctionService, times(1)).getBidderIds(anyInt());
        verify(auctionService, times(1)).getTopBidAuctions(anyInt());
    }

    @Test
    void getUnfinishedRecommendedAuctions_returnsSuccess() {
        when(auctionService.getAllAuctionsByUserId(anyInt())).thenReturn(expectedAuctionList);
        when(auctionService.getBidderIds(anyInt())).thenReturn(Collections.singletonList(user.getId()));
        when(auctionService.getTopBidAuctions(anyInt())).thenReturn(expectedAuctionList);

        List<Auction> recommendedAuctionedForUser = uut.getUnfinishedRecommendedAuctions(user);

        assertNotNull(recommendedAuctionedForUser);
        assertEquals(expectedAuctionList, recommendedAuctionedForUser);
        verify(auctionService, times(1)).getAllAuctionsByUserId(anyInt());
        verify(auctionService, times(1)).getBidderIds(anyInt());
        verify(auctionService, times(1)).getTopBidAuctions(anyInt());
    }
}