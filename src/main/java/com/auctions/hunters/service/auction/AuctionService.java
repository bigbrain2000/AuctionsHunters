package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.model.enums.AuctionStatus;
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
     * RetrieveS a list with all the {@link Auction} objects from the database which have ACTIVE status.
     *
     * @return a {@link List} of {@link Auction} objects if exists, empty otherwise
     */
    List<Auction> findAllActiveAuctions();

    /**
     * Retrieves an {@link Auction} object from the database where the foreign key, car_id is equal to the specified parameter value.
     *
     * @param carId the ID of the {@link Car}, which is the FK of the {@link Auction} table
     * @return an {@link Auction} object if found in the database, or null if not found
     */
    Auction getAuctionByCarId(Integer carId);

    /**
     * Retrieves from the database a list of {@link Auction} objects for the given {@link User} id.
     *
     * @param userId the ID of the user for which the auctions will be retrieved from the database
     * @return a list of {@link Auction} objects for the found {@link User} id, or an empty list if not found
     */
    List<Auction> getAllAuctionsByUserId(Integer userId);

    /**
     * Retrieves a limited by size list with all the ACTIVE {@link Auction} objects from the database that have the most bidders.
     * <p>
     * Sort descendent the auctions list to het the top bidders, filter only the auctions that have {@link AuctionStatus#ACTIVE}
     * as status and limit the number of list elements by the parameter value.
     *
     * @param limit the list limitation size
     * @return a list of {@link Auction} objects if the bidders bid on auctions / an empty list otherwise
     */
    List<Auction> getTopBidAuctions(int limit);

    /**
     * Retrieve a list with all the {@link User} ids who bid on the {@link Auction} specified by the parameter id.
     *
     * @param auctionId the {@link Auction} id for which the bidders ids will be retrieved
     * @return a list of {@link Integer} objects if the bidders bid on the specified auction id parameter / an empty list otherwise
     */
    List<Integer> getBidderIds(Integer auctionId);

    /**
     * Method used for setting the price of all {@link Car} objects that are listed in the auctions.
     */
    List<Float> setCurrentPriceForEachCarPage(Page<Car> carPage);

    /**
     * Update the current price of an {@link Auction} that`s live.
     *
     * @param auctionId    persisted {@link Auction} auctionId
     * @param currentPrice the new price of the {@link Auction}
     */
    void updateAuctionCurrentPrice(Integer auctionId, float currentPrice, Integer buyerId);

    /**
     * Manages all the finished auctions. The finished auctions are removed from the live table of {@link Auction}
     */
    Page<Car> manageFinishedAuctions(User user, Page<Car> carPage);

    /**
     * Method used for retrieving a list of finished {@link Auction} objects and update their status from
     * ACTIVE to SOLD.
     */
    void updateFinishedAuctionsStatusAsSold();

    /**
     * Retrieves a list of {@link Car} from the database by the id of the new owner
     *
     * @return A list of {@link Car} objects, containing all the cars won by the user identified by the id param.
     * If no cars are found for the given buyer, an empty list is returned.
     */
    List<Car> getCarsFromFinishedAuctionsForBuyerId();

    /**
     * Retrieve a list of {@link Float} objects that represents the final prices for the finished auctions.
     */
    List<Float> getFinishedAuctionsCurrentPrice();

    /**
     * Get the total price that the user needs to pay for the bought cars.
     *
     * @return a {@link Float} number representing the auctions finals price that the user needs to pay for.
     */
    Float getTotalPriceToPay();

    /**
     * Retrieves a list of {@link Auction} from the database where based on the cars list provided.
     */
    List<Auction> findAuctionsByCars(List<Car> cars);
}

