package com.auctions.hunters.service.bid;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Bid} entity.
 */
public interface BidService {
    Bid save(float amount, Auction auction);
}
