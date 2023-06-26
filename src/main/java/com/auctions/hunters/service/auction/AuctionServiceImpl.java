package com.auctions.hunters.service.auction;

import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.model.enums.AuctionStatus;
import com.auctions.hunters.model.enums.CarStatus;
import com.auctions.hunters.repository.AuctionRepository;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.auctions.hunters.model.enums.AuctionStatus.*;
import static com.auctions.hunters.model.enums.CarStatus.AUCTIONED;
import static com.auctions.hunters.utils.DateUtils.getDateTime;
import static java.util.List.of;

@Slf4j
@Service
@Transactional
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserService userService;
    private final CarService carService;

    public AuctionServiceImpl(AuctionRepository auctionRepository,
                              UserService userService,
                              CarService carService) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
        this.carService = carService;
    }

    /**
     * Creates an {@link Auction} object and save it in the database based on the provided car id and logged username.
     * <p>
     * The default time to live for an {@link Auction} is 1 day. After that, then it will close.
     * When creating an {@link Auction}, the user needs to enter the minimum price that he will agree to sell the car for.
     * The status of a live {@link Auction} is {@link AuctionStatus#ACTIVE}.
     * the {@link Car} entity that will be auctioned
     *
     * @param minimumPrice the
     *                     * @param car  minimum price that the seller demands for his car
     * @return the {@link Auction} model that was persisted in the database
     */
    @Override
    public Auction save(Car car, float minimumPrice) {
        User user = userService.findByUsername(userService.getLoggedUsername());
        OffsetDateTime now = OffsetDateTime.now();

        Auction newAuction = Auction.builder()
                .car(car)
                .user(user)
                .startTime(now)
                .endTime(now.plusMinutes(10))  //end date is current date + 1 day
                .minimumPrice(minimumPrice)
                .startingPrice(minimumPrice)
                .currentPrice(minimumPrice)
                .status(ACTIVE)
                .build();

        //if a car is placed in an auction then change it`s status
        carService.updateCarAuctionStatus(car.getId(), AUCTIONED);

        auctionRepository.save(newAuction);
        log.debug("Auction with id {} has been created.", newAuction.getId());

        return newAuction;
    }

    /**
     * RetrieveS a list with all the {@link Auction} objects from the database which have ACTIVE status.
     *
     * @return a {@link List} of {@link Auction} objects if exists, empty otherwise
     */
    @Override
    public List<Auction> findAllActiveAuctions() {
        List<Auction> auctionList = auctionRepository.findAll().stream()
                .filter(auction -> auction.getStatus().equals(ACTIVE))
                .toList();

        if (auctionList.isEmpty()) {
            log.debug("The auctions list was empty.");
            return of();
        }

        log.debug("The auctions list was retrieved from the database.");
        return new ArrayList<>(auctionList);
    }

    /**
     * Retrieves an {@link Auction} object from the database where the foreign key, car_id is equal to the specified parameter value.
     *
     * @param carId the ID of the {@link Car}, which is the FK of the {@link Auction} table
     * @return an {@link Auction} object if found in the database, or null if not found
     */
    @Override
    public Auction getAuctionByCarId(Integer carId) {
        return auctionRepository.findByCarId(carId);
    }

    /**
     * Retrieves from the database a list of {@link Auction} objects for the given {@link User} id.
     *
     * @param userId the ID of the user for which the auctions will be retrieved from the database
     * @return a list of {@link Auction} objects for the found {@link User} id, or an empty list if not found
     */
    @Override
    public List<Auction> getAllAuctionsByUserId(Integer userId) {
        List<Auction> userAuctionsList = auctionRepository.findByUserId(userId);

        if (userAuctionsList == null) {
            log.debug("Could not find the auctions list by user id {} ", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return userAuctionsList;
    }

    /**
     * Retrieves a limited by size list with all the ACTIVE {@link Auction} objects from the database that have the most bidders.
     * <p>
     * Sort descendent the auctions list to het the top bidders, filter only the auctions that have {@link AuctionStatus#ACTIVE}
     * as status and limit the number of list elements by the parameter value.
     *
     * @param limit the list limitation size
     * @return a list of {@link Auction} objects if the bidders bid on auctions / an empty list otherwise
     */
    @Override
    public List<Auction> getTopBidAuctions(int limit) {
        return auctionRepository.findAll().stream()
                // the auctions with the most bidders will be listed
                .sorted(Comparator.comparing(a -> a.getBidders().size(), Comparator.reverseOrder()))
                .filter(auction -> auction.getStatus().equals(ACTIVE))
                .limit(limit)
                .toList();
    }

    /**
     * Retrieve a list with all the {@link User} ids who bid on the {@link Auction} specified by the parameter id.
     *
     * @param auctionId the {@link Auction} id for which the bidders ids will be retrieved
     * @return a list of {@link Integer} objects if the bidders bid on the specified auction id parameter / an empty list otherwise
     */
    @Override
    public List<Integer> getBidderIds(Integer auctionId) {
        List<Bid> bids = getAllBidsByAuctionId(auctionId);
        return bids.stream()
                .map(bid -> bid.getUser().getId())
                .toList();
    }

    /**
     * Retrieves from the database a list of {@link Bid} objects for the given {@link Auction} id.
     *
     * @param auctionId the ID of the auction to retrieve the bids from the database
     * @return a list of {@link Bid} objects for the found {@link Auction} id, or an empty list if not found
     */
    private List<Bid> getAllBidsByAuctionId(Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElse(null);

        if (auction != null) {
            List<Bid> bidders = auction.getBidders();
            bidders.size(); // This line will force Hibernate to fetch the lazy-loaded data
            return bidders;
        }

        return new ArrayList<>();
    }

    /**
     * Method used for setting the price of all {@link Car} objects that are listed in the auctions.
     */
    @Override
    public List<Float> setCurrentPriceForEachCarPage(Page<Car> carPage) {
        List<Float> auctionsCurrentPriceList = new ArrayList<>();
        List<Car> carList = carPage.getContent();

        for (Car car : carList) {
            Auction auction = getAuctionByCarId(car.getId());

            if (auction != null) {
                auctionsCurrentPriceList.add(auction.getCurrentPrice());
            } else {
                auctionsCurrentPriceList.add(0f);
            }
        }

        return auctionsCurrentPriceList;
    }

    /**
     * Update the current price of an {@link Auction} that`s live.
     *
     * @param auctionId    persisted {@link Auction} auctionId
     * @param currentPrice the new price of the {@link Auction}
     */
    @Override
    public void updateAuctionCurrentPrice(Integer auctionId, float currentPrice, Integer buyerId) {
        String errorMessage = String.format("Auction with the id: %s was not found!", auctionId);

        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new IllegalArgumentException(errorMessage));
        log.debug("Successfully retrieved auction with id {}", auction.getId());

        auction.setCurrentPrice(currentPrice);
        auction.setBuyerId(buyerId);

        // Save the updated car back to the database
        auctionRepository.save(auction);
    }

    /**
     * Manages all the finished auctions. The finished auctions are removed from the live table of {@link Auction}
     * The return type is {@link Page<Car>} because it will be used to update the live tables.
     */
    @Override
    public Page<Car> manageFinishedAuctions(User user, Page<Car> carPage) {
        log.debug("Start processing the finished auctions.");

        List<Auction> finishedUserAuctionsList = retrieveAllFinishedAuctionsFromACarPage(user);

        Page<Car> updatedCarPage = updateLiveAuctionsIntoFinishAuctions(finishedUserAuctionsList, carPage);

        log.debug("Stop processing the finished auctions.");
        return updatedCarPage;
    }

    /**
     * Manages all the finished auctions. The finished auctions are removed from the live table of {@link Auction}
     * The return type is void because it will be used in the "/payment" template.
     */
    @Override
    public void manageFinishedAuctions() {
        String loggedUsername = userService.getLoggedUsername();
        User user = userService.findByUsername(loggedUsername);

        log.debug(String.format("Start processing the finished auctions for %s.", loggedUsername));

        List<Auction> finishedUserAuctionsList = retrieveAllFinishedAuctionsFromACarPage(user);

        updateLiveAuctionsIntoFinishAuctions(finishedUserAuctionsList);

        log.debug("Stop processing the finished auctions.");
    }

    /**
     * Retrieves a list of {@link Auction} objects, representing the finished auctions from the car pages.
     *
     * @param buyer the user for whom the {@link Auction} objects are retrieved
     * @return a list of finished {@link Auction} objects
     */
    private List<Auction> retrieveAllFinishedAuctionsFromACarPage(User buyer) {
        //filter the auctions list to see if they are finished
        OffsetDateTime now = getDateTime();
        List<Auction> endedAuctions = auctionRepository.findAll().stream()
                .filter(auction -> now.isAfter(auction.getEndTime()))
                .toList();

        List<Auction> endedAuctionsForBuyer = endedAuctions.stream()
                .filter(auction -> auction.getBuyerId() != null && auction.getBuyerId().equals(buyer.getId()))
                .toList();

        List<Auction> finishedAuctions = endedAuctionsForBuyer.stream()
                .filter(auction -> auction.getStatus().equals(ACTIVE))
                .toList();

        String logMessage = String.format("Retrieved %d finished won auctions for user %s", finishedAuctions.size(), buyer.getUsername());
        log.info(logMessage);

        return finishedAuctions;
    }

    /**
     * Method used for retrieving a list of finished {@link Auction} objects and update their status from
     * ACTIVE to SOLD.
     *
     * @param finishedAuctionList the retrieved auction list.
     * @param carPage             the car page from where the auctions are retrieved.
     */
    private Page<Car> updateLiveAuctionsIntoFinishAuctions(List<Auction> finishedAuctionList, Page<Car> carPage) {
        List<Car> carList = finishedAuctionList.stream()
                .map(Auction::getCar)
                .toList();

        List<Car> updatedCarList = carList.stream()
                .map(car -> carService.updateCarAuctionStatus(car.getId(), CarStatus.SOLD))
                .toList();

        List<Integer> carIdsList = carList.stream()
                .map(Car::getId)
                .toList();

        if (!updatedCarList.isEmpty()) {
            // Update the status of all finished auctions to CLOSED
            updateAuctionStatus(finishedAuctionList, CLOSED);

            // Save the updated auctions to the database
            updateAuctionList(finishedAuctionList);

            // Filter the carPage content to exclude finished auctions
            List<Car> filteredCarList = carPage.getContent().stream()
                    .filter(car -> !updatedCarList.contains(car))
                    .toList();

            Page<Car> updatedPage = new PageImpl<>(filteredCarList, carPage.getPageable(), carPage.getTotalElements() - updatedCarList.size());

            log.debug(String.format("%s cars with IDs %s were deleted from the live finishedAuctionList.", updatedCarList.size(), carIdsList));
            return updatedPage;
        }

        log.debug("No cars were found to be processed as the list of finished finishedAuctionList is empty.");
        return carPage;
    }

    private void updateLiveAuctionsIntoFinishAuctions(List<Auction> finishedAuctionList) {
        List<Car> carList = finishedAuctionList.stream()
                .map(Auction::getCar)
                .toList();

        List<Car> updatedCarList = carList.stream()
                .map(car -> carService.updateCarAuctionStatus(car.getId(), CarStatus.SOLD))
                .toList();

        List<Integer> carIdsList = carList.stream()
                .map(Car::getId)
                .toList();

        if (!updatedCarList.isEmpty()) {
            // Update the status of all finished auctions to CLOSED
            updateAuctionStatus(finishedAuctionList, CLOSED);

            // Save the updated auctions to the database
            updateAuctionList(finishedAuctionList);

            log.debug(String.format("%s cars with IDs %s were deleted from the live finishedAuctionList.", updatedCarList.size(), carIdsList));
        }

        log.debug("No cars were found to be processed as the list of finished finishedAuctionList is empty.");
    }

    /**
     * Updates the list of {@link Auction} objects in the database.
     * The method saves all the items in the list using the {@link AuctionRepository#saveAll} method.
     * If an auction with the same ID already exists in the database, it will be updated with the values from the provided object.
     * If it does not exist, a new auction will be created.
     *
     * @param auctionList a {@link List} of {@link Auction} objects to be updated or saved in the database
     */
    private void updateAuctionList(List<Auction> auctionList) {
        auctionRepository.saveAll(auctionList);
    }

    /**
     * Update the status of all finished auctions to SOLD.
     *
     * @param finishedAuctionList the finished auction list
     * @param status              the status to be changed to
     */
    private void updateAuctionStatus(List<Auction> finishedAuctionList, AuctionStatus status) {
        log.debug("Starting updating finished auction list to status {}.", status);
        finishedAuctionList.forEach(auction -> auction.setStatus(status));
        log.debug("Update complete.");
    }

    /**
     * Retrieves a list of CLOSED {@link Auction} from the database where the specified buyer has won.
     *
     * @return A list of {@link Auction} objects, containing all the finished auctions won by the specified buyer.
     * If no auctions are found for the given buyer, an empty list is returned.
     */
    private List<Auction> getClosedAuctionsByUserId() {
        String loggedUsername = userService.getLoggedUsername();
        User buyer = userService.findByUsername(loggedUsername);

        List<Auction> userFinishedAuctionList = auctionRepository.findAllByBuyerIdAndStatus(buyer.getId(), CLOSED);
        log.debug("Retrieved {} CLOSED auctions.", userFinishedAuctionList.size());

        return userFinishedAuctionList;
    }

    /**
     * Retrieves a list of SOLD {@link Auction} from the database where the specified buyer has won.
     *
     * @return A list of {@link Auction} objects, containing all the finished auctions won by the specified buyer.
     * If no auctions are found for the given buyer, an empty list is returned.
     */
    public List<Auction> getSoldAuctionsByUserId() {
        String loggedUsername = userService.getLoggedUsername();
        User buyer = userService.findByUsername(loggedUsername);

        List<Auction> userFinishedAuctionList = auctionRepository.findAllByBuyerIdAndStatus(buyer.getId(), SOLD);
        log.debug("Retrieved {} SOLD auctions.", userFinishedAuctionList.size());

        return userFinishedAuctionList;
    }

    /**
     * Method used for retrieving a list of finished {@link Auction} objects and update their status from
     * ACTIVE to SOLD.
     */
    @Override
    public void updateFinishedAuctionsStatusAsSold() {
        List<Auction> finishedAuctionsByUserId = getClosedAuctionsByUserId();

        updateAuctionStatus(finishedAuctionsByUserId, SOLD);
    }

    /**
     * Retrieves a list of {@link Car} from the database by the id of the new owner
     *
     * @return A list of {@link Car} objects, containing all the cars won by the user identified by the id param.
     * If no cars are found for the given buyer, an empty list is returned.
     */
    @Override
    public List<Car> getCarsFromFinishedAuctionsForBuyerId() {
        List<Auction> finishedAuctions = getClosedAuctionsByUserId();

        List<Integer> carsIdList = finishedAuctions.stream()
                .map(auction -> auction.getCar().getId())
                .toList();

        List<Car> carToBeBoughtList = carService.findAllByIdIn(carsIdList);
        log.debug("Retrieved {} cars to be purchased", carToBeBoughtList.size());
        return carToBeBoughtList;
    }

    /**
     * Retrieve a list of {@link Float} objects that represents the final prices for the finished auctions.
     */
    @Override
    public List<Float> getFinishedAuctionsCurrentPrice() {
        return getClosedAuctionsByUserId().stream()
                .map(Auction::getCurrentPrice)
                .toList();
    }

    /**
     * Get the total price that the user needs to pay for the bought cars.
     *
     * @return a {@link Float} number representing the auctions finals price that the user needs to pay for.
     */
    @Override
    public Float getTotalPriceToPay() {
        return getFinishedAuctionsCurrentPrice().stream()
                .reduce(0.0f, Float::sum);
    }

    /**
     * Retrieves a list of {@link Auction} from the database where based on the cars list provided.
     */
    @Override
    public List<Auction> findAuctionsByCars(List<Car> cars) {
        return auctionRepository.findByCarIn(cars);
    }
}
