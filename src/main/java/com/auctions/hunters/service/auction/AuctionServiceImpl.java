package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.AuctionRepository;
import com.auctions.hunters.service.user.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;

@Slf4j
@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final SellerService sellerService;

    public AuctionServiceImpl(AuctionRepository auctionRepository,
                              SellerService sellerService) {
        this.auctionRepository = auctionRepository;
        this.sellerService = sellerService;
    }

    @Override
    public Auction save(Car car, Auction auction) {
        User user = sellerService.findByUsername(sellerService.getLoggedUsername());
        OffsetDateTime now = OffsetDateTime.now();

        Auction newAuction = Auction.builder()
                .car(car)
                .sellerName(user.getUsername())
                .startTime(now)
                .endTime(now.plusDays(1))  //end date is current date + 1
                .minimumPrice(auction.getMinimumPrice())
                .startingPrice(auction.getStartingPrice())
                .currentPrice(auction.getStartingPrice())
                .isActive(true)
                .build();

        auctionRepository.save(newAuction);
        log.debug("Auction has been saved in the database.");

        return newAuction;
    }

    @Override
    public List<Auction> findAll() {
        List<Auction> auctionList = auctionRepository.findAll();

        if (auctionList.isEmpty()) {
            log.debug("The auction list was empty.");
            return of();
        }

        log.debug("The auction list was retrieved from the database.");
        return new ArrayList<>(auctionList);
    }
}
