package com.auctions.hunters.service.auction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.AuctionRepository;
import com.auctions.hunters.service.user.SellerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

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

        Auction newAuction = Auction.builder()
                .car(car)
                .sellerName(user.getUsername())
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plusDays(1))  //end date is current date + 1
                .startingPrice(auction.getStartingPrice())
                .currentPrice(auction.getStartingPrice())
                .build();

        while (!newAuction.getStartTime().equals(newAuction.getEndTime())) {
            newAuction.setActive(true);
        }

        auctionRepository.save(newAuction);
        return newAuction;
    }
}
