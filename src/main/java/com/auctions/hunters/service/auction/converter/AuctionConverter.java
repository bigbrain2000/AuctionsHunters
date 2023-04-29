package com.auctions.hunters.service.auction.converter;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.FinishedAuction;
import org.springframework.core.convert.ConversionException;
import org.springframework.stereotype.Component;

@Component
public class AuctionConverter {

    /**
     * Convert an object of type {@link Auction} into a {@link FinishedAuction} type.
     *
     * @param auction the source object to be converted
     * @return the converted object, an instance of {@link FinishedAuction}
     */
    public FinishedAuction convertToFinishedAuction(Auction auction) {
        return FinishedAuction.builder()
                .buyerId(auction.getBuyerId())
                .sellerId(auction.getUser().getId())
                .carId(auction.getCar().getId())
                .build();
    }
}
