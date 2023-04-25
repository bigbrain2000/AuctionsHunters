package com.auctions.hunters.service.bid;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.User;

import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Bid} entity.
 */
public interface BidService {

    Bid save(float amount, Auction auction);

    List<Auction> findAuctionsByUser(User user);

    List<Bid> findUserBidsForAuction(User user, int auctionId);

    public List<Bid> findAllBidsByUser(User user);
    }
