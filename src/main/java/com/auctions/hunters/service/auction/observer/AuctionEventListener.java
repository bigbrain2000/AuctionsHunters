package com.auctions.hunters.service.auction.observer;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.auction.AuctionService;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuctionEventListener {

    private final AuctionService auctionService;

    private final ConcurrentHashMap<Integer, Page<Car>> updatedCarPages = new ConcurrentHashMap<>();

    public AuctionEventListener(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @EventListener
    public void handleFinishedAuctionEvent(AuctionEvent auctionEvent) {
        List<Auction> finishedAuctions = auctionEvent.getFinishedAuctions();
        Page<Car> carPage = auctionEvent.getCarPage();

        Page<Car> updatedCarPage = auctionService.updateLiveAuctionsIntoFinishAuctions(finishedAuctions, carPage);

        // Store the updated carPage in the shared data structure
        updatedCarPages.put(auctionEvent.getCarPageKey(), updatedCarPage);
    }

    public Page<Car> getUpdatedCarPage(int key) {
        return updatedCarPages.get(key);
    }
}