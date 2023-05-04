package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Auction} entity.
 */
public interface AuctionService {

    void checkAndPublishFinishedAuctions(User user, Page<Car> carPage, int carPageKey);

    /**
     * Save an auction in the database based on the provided car id and logged username.
     *
     * @param car          the car that will be put on an auction
     * @param minimumPrice the minimum price that the seller demands for his car
     * @return the auction model that was persisted in the database
     */
    Auction save(Car car, float minimumPrice);

    /**
     * Insert new auctions or update existing entities in the database.
     *
     * @param auctionList the list of {@link Auction} objects to be updated
     */
    void updateAuctionList(List<Auction> auctionList);

    /**
     * Retrieve a list with all the auctions from the database.
     *
     * @return the auctions list
     */
    List<Auction> findAll();

    /**
     * Retrieved an {@link Auction} from the database where the foreign key, car_id is equal to the parameter value.
     */
    Auction getAuctionByCarId(Integer carId);

    /**
     * Retrieves from the database a list of {@link Auction} objects for the given {@link User} id.
     *
     * @param userId the ID of the user for which the auctions will be retrieved from the database
     * @return a list of {@link Auction} objects for the found {@link User} id, or an empty list if not found
     */
    List<Auction> getAllAuctionsByUserId(Integer userId);

    List<Bid> getAllBidsByAuctionId(Integer auctionId);

    /**
     * Retrieve a list with all the {@link User} ids who bid on the {@link Auction} specified by the parameter id.
     *
     * @param auctionId the {@link Auction} id for which the bidders ids will be retrieved
     * @return a list of {@link Integer} objects if the bidders bid on the specified auction id parameter / an empty list otherwise
     */
    List<Integer> getBidderIds(Integer auctionId);

    /**
     * Retrieve a limited by size list with all the ACTIVE {@link Auction} objects from the database that have the most bidders.
     *
     * @param limit the list limitation size
     * @return a list of {@link Auction} objects if the bidders bid on auctions / an empty list otherwise
     */
    List<Auction> getTopBidAuctions(int limit);

    List<Float> setCurrentPriceForEachCarPage(Page<Car> carPage);

    List<Auction> retrieveAllFinishedAuctionsFromACarPage(Page<Car> carPage, User user);

    Auction updateAuctionCurrentPrice(Integer auctionId, float currentPrice, Integer buyerId);

    Page<Car> updateLiveAuctionsIntoFinishAuctions(List<Auction> auctions, Page<Car> carPage);

    Page<Car> manageFinishedAuctions(User user, Page<Car> carPage);

    List<Car> getCarsFromFinishedAuctionsForBuyerId();

    void updateFinishedAuctionsStatusAsSold();

    List<Float> getFinishedAuctionsCurrentPrice();

    Float getTotalPriceToPay();
}

