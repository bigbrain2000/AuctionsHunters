package com.auctions.hunters.service.auction;

import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.AuctionRepository;
import com.auctions.hunters.repository.BidRepository;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.util.List.of;

@Slf4j
@Service
@Transactional
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserService userService;
    private final CarService carService;
    private final BidRepository bidRepository;

    public AuctionServiceImpl(AuctionRepository auctionRepository,
                              UserService userService,
                              CarService carService,
                              BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
        this.carService = carService;
        this.bidRepository = bidRepository;
    }


    /**
     * Save an auction in the database based on the provided car id and logged username.
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
        log.debug("Auction has been saved in the database.");

        return newAuction;
    }

    /**
     * Retrieve a list with all the auctions from the database.
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

    @Override
    public Auction getAuctionByCarId(Integer carId) {
        return auctionRepository.findByCarId(carId);
    }

    public List<Bid> getAllBidsForAuction(Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElse(null);
        if (auction != null) {
            List<Bid> bidders = auction.getBidders();
            bidders.size(); // This line will force Hibernate to fetch the lazy-loaded data
            return bidders;
        }
        return new ArrayList<>();
    }

    public List<Auction> getAllAuctionsForBidder(Integer userId) {
        List<Bid> bids = bidRepository.findByUserId(userId);
        return bids.stream()
                .map(Bid::getAuction)
                .toList();
    }

    public List<Auction> getAllAuctionsByUserId(Integer userId) {
        List<Auction> userAuctionsList = auctionRepository.findByUserId(userId);

        if(userAuctionsList == null) {
            log.debug("Could not find the auctions list by user id {} ", userId);
            throw  new ResourceNotFoundException("User", "id", userId);
        }

        return userAuctionsList;
    }

    @Override
    public Auction findById(Integer id) {
        return auctionRepository.findById(id).orElseThrow(() -> {
            log.debug("Could not find the auction by the id {} ", id);
            return new ResourceNotFoundException("Auction", "id", id);
        });
    }

    public List<Auction> getTopBidAuctions(int limit) {
        return auctionRepository.findAll().stream()
                .sorted(Comparator.comparing(a -> a.getBidders().size(), Comparator.reverseOrder()))
                .filter(Auction::isActive)
                .limit(limit)
                .toList();
    }

    public List<Integer> getBidderIds(Integer auctionId) {
        List<Bid> bids = getAllBidsForAuction(auctionId);
        return bids.stream().map(bid -> bid.getUser().getId()).collect(Collectors.toList());
    }

    @NotNull
    public List<Float> setMinimumPriceForEachPageCar(Page<Car> carPage) {
        List<Float> auctionsMinimumPriceList = new ArrayList<>();
        List<Car> content = carPage.getContent();
        for (Car car : content) {
            Auction auction = getAuctionByCarId(car.getId());

            if (auction != null) {
                auctionsMinimumPriceList.add(auction.getMinimumPrice());
            } else {
                auctionsMinimumPriceList.add(0f);
            }
        }

        return auctionsMinimumPriceList;
    }

}
