package com.auctions.hunters.service.auction.observer;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class AuctionEvent extends ApplicationEvent {

    private final List<Auction> finishedAuctions;
    private final Page<Car> carPage;
    private final int carPageKey;

    public AuctionEvent(Object source, List<Auction> finishedAuctions, Page<Car> carPage, int carPageKey) {
        super(source);
        this.finishedAuctions = finishedAuctions;
        this.carPage = carPage;
        this.carPageKey = carPageKey;
    }
}
