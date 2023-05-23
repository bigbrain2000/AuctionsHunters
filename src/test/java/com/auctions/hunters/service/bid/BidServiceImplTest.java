package com.auctions.hunters.service.bid;

import com.auctions.hunters.exceptions.LowBidAmountException;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.BidRepository;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @Mock
    private BidRepository bidRepository;
    @Mock
    private UserService userService;
    @Mock
    private AuctionService auctionService;
    @Mock
    private Bid bid;

    private BidService uut;

    private User user;
    private Auction auction;
    private List<Auction> expectedAuctionList;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new BidServiceImpl(bidRepository, userService, auctionService));

        user = new User();
        user.setId(1);
        user.setUsername("Alex");

        auction = new Auction();
        auction.setId(1);
        auction.setCurrentPrice(110);

        expectedAuctionList = List.of(auction);
        user.setAuctions(expectedAuctionList);
    }

    @Test
    void save_bidWithHigherAmount_returnsNewBid() throws LowBidAmountException {
        int actualAuctionPrice = 120;
        when(userService.getLoggedUsername()).thenReturn(user.getUsername());
        when(userService.findByUsername(anyString())).thenReturn(user);
        doNothing().when(auctionService).updateAuctionCurrentPrice(anyInt(), anyFloat(), anyInt());
        when(bidRepository.save(any(Bid.class))).thenReturn(bid);

        Bid actualBid = uut.save(actualAuctionPrice, auction);

        assertEquals(actualBid.getId(), bid.getId());
        assertTrue(actualAuctionPrice > auction.getCurrentPrice());
        assertEquals(actualBid.getAuction(), bid.getAuction());
        assertEquals(actualBid.getUser(), bid.getUser());
    }

    @Test
    void save_bidWithLowerAmount_throwsException() {
        int actualAuctionPrice = 100;

        assertThrows(LowBidAmountException.class, () -> uut.save(actualAuctionPrice, auction));
    }

    @Test
    void findAuctionsByUser() {
        when(bidRepository.findAuctionsByUser(user)).thenReturn(expectedAuctionList);

        List<Auction> auctionsByUser = uut.findAuctionsByUser(user);

        assertEquals(expectedAuctionList, auctionsByUser);
    }

    @Test
    void findUserBidsForAuction() {
        Set<Bid> expectedUserBidsForAuction = new HashSet<>();
        expectedUserBidsForAuction.add(bid);
        user.setBids(expectedUserBidsForAuction);
        when(bidRepository.findUserBidsForAuction(user, auction.getId())).thenReturn(expectedUserBidsForAuction.stream().toList());

        List<Bid> actualUserBidsForAuction = uut.findUserBidsForAuction(user, auction.getId());

        assertEquals(expectedUserBidsForAuction.stream().toList(), actualUserBidsForAuction);
    }
}