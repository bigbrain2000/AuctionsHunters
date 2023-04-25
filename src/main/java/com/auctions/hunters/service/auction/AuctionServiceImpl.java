package com.auctions.hunters.service.auction;

import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.AuctionRepository;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Boolean.TRUE;
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
     * Save an {@link Auction} in the database based on the provided car id and logged username.
     *
     * @param car          the car that will be put on an auction
     * @param minimumPrice the minimum price that the seller demands for his car
     * @return the auction model that was persisted in the database
     */
    @Override
    public Auction save(Car car, float minimumPrice) {
        User user = userService.findByUsername(userService.getLoggedUsername());
        OffsetDateTime now = OffsetDateTime.now();

        Auction newAuction = Auction.builder()
                .car(car)
                .user(user)
                .startTime(now)
                .endTime(now.plusDays(1))  //end date is current date + 1
                .minimumPrice(minimumPrice)
                .startingPrice(minimumPrice)
                .currentPrice(minimumPrice)
                .isActive(true)
                .build();

        //if a car is placed on an auction then change it`s status
        carService.updateCarAuctionStatus(car.getId(), TRUE);

        auctionRepository.save(newAuction);
        log.debug("Auction with id {} has been create.", newAuction.getId());

        return newAuction;
    }

    /**
     * Retrieve a list with all the {@link Auction} from the database.
     *
     * @return the auctions list if exists, empty otherwise
     */
    @Override
    public List<Auction> findAll() {
        List<Auction> auctionList = auctionRepository.findAll();

        if (auctionList.isEmpty()) {
            log.debug("The auctions list was empty.");
            return of();
        }

        log.debug("The auctions list was retrieved from the database.");
        return new ArrayList<>(auctionList);
    }

    /**
     * Retrieves an {@link Auction} from the database where the foreign key, car_id is equal to the specified parameter value.
     *
     * @param carId the ID of the car associated with the auction to retrieve from the database
     * @return an {@link Auction} object if found in the database, or null if not found
     */
    @Override
    public Auction getAuctionByCarId(Integer carId) {
        return auctionRepository.findByCarId(carId);
    }

    /**
     * Retrieves from the database a list of {@link Bid} objects for the given {@link Auction} id.
     *
     * @param auctionId the ID of the auction to retrieve from the database
     * @return a list of {@link Bid} objects for the found {@link Auction} id, or an empty list if not found
     */
    public List<Bid> getAllBidsByAuctionId(Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElse(null);

        if (auction != null) {
            List<Bid> bidders = auction.getBidders();
            bidders.size(); // This line will force Hibernate to fetch the lazy-loaded data
            return bidders;
        }

        return new ArrayList<>();
    }

    /**
     * Retrieves from the database a list of {@link Auction} objects for the given {@link User} id.
     *
     * @param userId the ID of the user for which the auctions will be retrieved from the database
     * @return a list of {@link Auction} objects for the found {@link User} id, or an empty list if not found
     */
    public List<Auction> getAllAuctionsByUserId(Integer userId) {
        List<Auction> userAuctionsList = auctionRepository.findByUserId(userId);

        if (userAuctionsList == null) {
            log.debug("Could not find the auctions list by user id {} ", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return userAuctionsList;
    }

    /**
     * Find a specific {@link Auction} object based on id.
     *
     * @param auctionId the id of the wanted {@link Auction}
     * @return found {@link Auction}
     */
    @Override
    public Auction findById(Integer auctionId) {
        return auctionRepository.findById(auctionId).orElseThrow(() -> {
            log.debug("Could not find the auction by the auctionId {} ", auctionId);
            return new ResourceNotFoundException("Auction", "id", auctionId);
        });
    }

    /**
     * Retrieve a limited by size list with all the ACTIVE {@link Auction} objects from the database that have the most bidders.
     *
     * @param limit the list limitation size
     * @return a list of {@link Auction} objects if the bidders bid on auctions / an empty list otherwise
     */
    public List<Auction> getTopBidAuctions(int limit) {
        return auctionRepository.findAll().stream()
                // the auctions with the most bidders will be listed
                .sorted(Comparator.comparing(a -> a.getBidders().size(), Comparator.reverseOrder()))
                .filter(Auction::isActive)
                .limit(limit)
                .toList();
    }

    /**
     * Retrieve a list with all the {@link User} ids who bid on the {@link Auction} specified by the parameter id.
     *
     * @param auctionId the {@link Auction} id for which the bidders ids will be retrieved
     * @return a list of {@link Integer} objects if the bidders bid on the specified auction id parameter / an empty list otherwise
     */
    public List<Integer> getBidderIds(Integer auctionId) {
        List<Bid> bids = getAllBidsByAuctionId(auctionId);
        return bids.stream()
                .map(bid -> bid.getUser().getId())
                .toList();
    }


    public List<Float> setMinimumPriceForEachPageCar(Page<Car> carPage) {
        List<Float> auctionsCurrentPriceList = new ArrayList<>();
        List<Car> content = carPage.getContent();

        for (Car car : content) {
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
     * Update the minimuum price of an {@link Auction} that`s live.
     *
     * @param id           persisted {@link Auction} id
     * @param currentPrice the new price of the {@link Auction}
     * @return the new updated {@link Auction} that`s being saved in the database
     */
    public Auction updateAuctionMinimumPrice(Integer id, float currentPrice) {
        String errorMessage = String.format("Auction with the id: %s was not found!", id);

        Auction auction = auctionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(errorMessage));
        log.debug("Successfully retrieved auction with id {}", auction.getId());

        auction.setCurrentPrice(currentPrice);

        // Save the updated car back to the database
        return auctionRepository.save(auction);
    }
}
