package com.auctions.hunters.service.bid;

import com.auctions.hunters.exceptions.LowBidAmountException;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.User;

import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Bid} entity.
 */
public interface BidService {

    /**
     * Save a new {@link Bid} made by a {@link User} for the given {@code auction}.
     *
     * @param amount  the price that the new bidder offers
     * @param auction the {@link Auction} for whom the bid is made
     * @return the newly create {@link Bid} object that was persisted in the database
     */
    Bid save(float amount, Auction auction) throws LowBidAmountException;

    /**
     * Get a {@link List} with {@link Auction} objects based on the provided {@link User}.
     *
     * @return a list with {@link Auction} objects if the given user created some auctions before, empty otherwise
     */
    List<Auction> findAuctionsByUser(User user);

    /**
     * Get a {@link List} with all {@link Bid} objects that a user made at the {@link Auction} specified by auctionId.
     *
     * @return a list with {@link Bid} objects if the given user bid at the {@link Auction}, empty otherwise
     */
    List<Bid> findUserBidsForAuction(User user, int auctionId);
}
