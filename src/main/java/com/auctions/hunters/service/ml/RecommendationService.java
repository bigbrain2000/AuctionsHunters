package com.auctions.hunters.service.ml;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;

import java.util.List;

/**
 * This interface defines the contract for a service that manages {@link Auction} objects in the system.
 */
public interface RecommendationService {

    /**
     * Return a list with all the different {@link Car} objects retrieved from the recommended {@link Auction} list.
     *
     * @param user the user for which the auction cars will be returned
     * @return a list with {@link Car} objects
     */
    List<Car> getRecommendedAuctionedCarsForUser(User user);


    /**
     * Return a list with all the unfinished recommended {@link Auction} objects.
     *
     * @param user the user for which the auctions will be returned
     * @return a list with {@link Auction} objects
     */
    List<Auction> getUnfinishedAuctions(User user);
}
