package com.auctions.hunters.service.bid;

import com.auctions.hunters.exceptions.LowBidAmountException;
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

    /**
     * Save a new {@link Bid} made by a {@link User} for the given {@code auction}.
     * If the amount provided by the user is lower than the current auction price, then throw {@link LowBidAmountException}.
     * Before the new bid is saved in the database, the auction price will be updated with the new offered {@code amount}.
     *
     * @param amount  the price that the new bidder offers
     * @param auction the {@link Auction} for whom the bid is made
     * @return the newly create {@link Bid} object that was persisted in the database
     */
    @Override
    public Bid save(float amount, Auction auction) throws LowBidAmountException {

        if (amount < auction.getCurrentPrice()) {
            String exceptionMessage = String.format("%s bid amount is not higher than %s.", amount, auction.getCurrentPrice());
            log.debug(exceptionMessage);
            throw new LowBidAmountException(exceptionMessage);
        }

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

    /**
     * Get a {@link List} with {@link Auction} objects based on the provided {@link User}.
     *
     * @return a list with {@link Auction} objects if the given user created some auctions before, empty otherwise
     */
    @Override
    public List<Auction> findAuctionsByUser(User user) {
        return bidRepository.findAuctionsByUser(user);
    }

    /**
     * Get a {@link List} with all {@link Bid} objects that a user made at the {@link Auction} specified by auctionId.
     *
     * @return a list with {@link Bid} objects if the given user bid at the {@link Auction}, empty otherwise
     */
    @Override
    public List<Bid> findUserBidsForAuction(User user, int auctionId) {
        return bidRepository.findUserBidsForAuction(user, auctionId);
    }
}
