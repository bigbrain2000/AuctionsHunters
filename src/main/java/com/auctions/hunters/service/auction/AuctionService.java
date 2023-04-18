package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Auction} entity.
 */
public interface AuctionService {


    /**
     * Save an auction in the database based on the provided car id and logged username.
     *
     * @param car          the car that will be put on an auction
     * @param minimumPrice the minimum price that the seller demands for his car
     * @return the auction model that was persisted in the database
     */
    Auction save(Car car, float minimumPrice);

    /**
     * Retrieve a list with all the auctions from the database.
     *
     * @return the auctions list
     */
    List<Auction> findAll();

    Auction getAuctionByCarId(Integer carId);

    List<Auction> getAllAuctionsByUserId(Integer userId);

    Auction findById(Integer id);

    public List<Auction> getAllAuctionsForBidder(Integer userId);

    List<Bid> getAllBidsForAuction(Integer auctionId);

     List<Integer> getBidderIds(Integer auctionId);


        List<Auction> getTopBidAuctions(int limit);

    List<Float> setMinimumPriceForEachPageCar(Page<Car> carPage);
}

