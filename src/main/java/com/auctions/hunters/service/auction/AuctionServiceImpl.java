package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.AuctionRepository;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.List.of;

@Slf4j
@Service
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
                .sellerName(user.getUsername())
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
        Auction auction = auctionRepository.findByCarId(carId);

        if (auction == null) {
            log.debug("Could not retrieve the auction for car id {}", carId);
            return null;
        }

        log.debug("Auction {} retrieved for car {}", auction.getId(), carId);
        return auction;
    }
}
