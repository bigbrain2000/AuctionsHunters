package com.auctions.hunters.service.finishedauction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.FinishedAuction;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.repository.FinishedAuctionRepository;
import com.auctions.hunters.service.auction.AuctionService;
import com.auctions.hunters.service.auction.converter.AuctionConverter;
import com.auctions.hunters.service.car.CarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FinishedAuctionServiceImpl implements FinishedAuctionService {

    private final FinishedAuctionRepository finishedAuctionRepository;
    private final AuctionConverter auctionConverter;
    private final AuctionService auctionService;
    private final CarService carService;
    private final CarRepository carRepository;

    public FinishedAuctionServiceImpl(FinishedAuctionRepository finishedAuctionRepository,
                                      AuctionConverter auctionConverter,
                                      AuctionService auctionService,
                                      CarService carService, CarRepository carRepository) {
        this.finishedAuctionRepository = finishedAuctionRepository;
        this.auctionConverter = auctionConverter;
        this.auctionService = auctionService;
        this.carService = carService;
        this.carRepository = carRepository;
    }

    @Override
    public void save(List<Auction> auctions) {
        List<FinishedAuction> finishedAuctions = auctions.stream().map(auctionConverter::convertToFinishedAuction).toList();

        finishedAuctionRepository.saveAll(finishedAuctions);

        log.info(String.format("%s finished auctions were saved in the new database.", finishedAuctions));
    }

    /**
     * Manages all the finished auctions. The finished auctions are removed from the live table of {@link Auction}
     * and from the {@link Auction} table, and they are inserted in the new table, {@link FinishedAuction}.
     *
     * @param user
     * @param carPage
     */
    @Override
    public void manageFinishedAuctions(User user, Page<Car> carPage) {
        List<Auction> finishedUserAuctionsList = auctionService.retrieveAllFinishedAuctionsFromACarPage(carPage, user);

        if (!finishedUserAuctionsList.isEmpty()) {
            log.debug("Start processing the finished auctions.");

            auctionService.deleteFinishedAuctionsFromLiveAuctions(finishedUserAuctionsList, carPage);

            save(finishedUserAuctionsList);
            log.debug("Stop processing the finished auctions.");
        }
    }

    /**
     * Retrieves a list of {@link FinishedAuction} from the database where the specified buyer has won.
     *
     * @param buyerId The unique identifier of the buyer for whom to fetch the finished auctions.
     * @return A list of {@link FinishedAuction} objects, containing all the finished auctions won by the specified buyer.
     * If no auctions are found for the given buyer, an empty list is returned.
     */
    public List<FinishedAuction> getFinishedAuctionsByUserId(Integer buyerId) {
        return finishedAuctionRepository.findAllByBuyerId(buyerId);
    }

    /**
     * Retrieves a list of {@link Car} from the database by the id of the new owner
     *
     * @param buyer The unique identifier of the buyer for whom to fetch the finished auctions.
     * @return A list of {@link Car} objects, containing all the cars won by the user identified by the id param.
     * If no cars are found for the given buyer, an empty list is returned.
     */
    public List<Car> getCarsFromFinishedAuctionsForBuyer(Integer buyer) {
        List<FinishedAuction> finishedAuctions = getFinishedAuctionsByUserId(buyer);
        List<Integer> carsIdsList = finishedAuctions.stream()
                .map(FinishedAuction::getSellerId)
                .toList();

        return carRepository.findAllByUserIdIn(carsIdsList);
    }
}
