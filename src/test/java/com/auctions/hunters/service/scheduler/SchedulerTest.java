package com.auctions.hunters.service.scheduler;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.bid.BidService;
import com.auctions.hunters.service.email.EmailService;
import com.auctions.hunters.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

    @Mock
    private UserService userService;
    @Mock
    private BidService bidService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private Scheduler uut;

    @Test
    void sendEmailIfUserBidsWereOvertaken_userIsTopBidder_doesNotReceiveEmail() {
        Car car = new Car();
        car.setId(1);
        User user1 = new User();
        user1.setId(1);
        user1.setReminder(FALSE);
        user1.setCarList(Collections.singletonList(car));
        User user2 = new User();
        user2.setId(2);
        Auction auction = new Auction();
        auction.setId(1);
        auction.setCar(car);
        Bid bid = Bid.builder()
                .user(user1)
                .id(1)
                .auction(auction)
                .amount(11)
                .build();
        when(userService.findAll()).thenReturn(List.of(user1, user2));
        when(bidService.findAuctionsByUser(user1)).thenReturn(Collections.singletonList(auction));
        when(bidService.findUserBidsForAuction(user1, auction.getId())).thenReturn(Collections.singletonList(bid));

        uut.sendEmailIfUserBidsWereOvertaken();

        verify(userService, times(1)).findAll();
        verify(bidService, times(1)).findAuctionsByUser(user1);
        verify(bidService, times(1)).findUserBidsForAuction(user1, auction.getId());
    }
}