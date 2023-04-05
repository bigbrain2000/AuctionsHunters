package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Auction} entity.
 */
public interface AuctionService {

    Auction save(Car car, Auction auction);
}
