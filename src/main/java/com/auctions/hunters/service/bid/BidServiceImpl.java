package com.auctions.hunters.service.bid;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.BidRepository;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final UserService userService;
    private final AuctionService auctionService;

    public BidServiceImpl(BidRepository bidRepository,
                          UserService userService,
                          AuctionService auctionService) {
        this.bidRepository = bidRepository;
        this.userService = userService;
        this.auctionService = auctionService;
    }

    @Override
    public Bid save(float amount, Auction auction) {

        String loggedUsername = userService.getLoggedUsername();
        User user = userService.findByUsername(loggedUsername);

        Bid bid = Bid.builder()
                .amount(amount)
                .auction(auction)
                .user(user)
                .build();

        auctionService.updateAuctionCurrentPrice(auction.getId(), amount, user.getId());
        return bidRepository.save(bid);
    }

    @Override
    public List<Auction> findAuctionsByUser(User user) {
        return bidRepository.findAuctionsByUser(user);
    }

    @Override
    public List<Bid> findUserBidsForAuction(User user, int auctionId) {
        return bidRepository.findUserBidsForAuction(user, auctionId);
    }

    @Override
    public List<Bid> findAllBidsByUser(User user) {
        return bidRepository.findByUserId(user.getId());
    }
}
