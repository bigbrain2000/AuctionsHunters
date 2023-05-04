package com.auctions.hunters.service.auction.observer;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
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

    /**
     * The listener listen if the finished auctions were published.
     * If the listener is not executed then the map is not updated.
     *
     * @param auctionEvent the event that can be ApplicationEvent instances as well as arbitrary objects
     */
    @EventListener
    public void handleFinishedAuctionEvent(AuctionEvent auctionEvent) {
        List<Auction> finishedAuctions = auctionEvent.getFinishedAuctions();
        Page<Car> carPage = auctionEvent.getCarPage();

        Page<Car> updatedCarPage = auctionService.updateLiveAuctionsIntoFinishAuctions(finishedAuctions, carPage);

        // Store the updated carPage in the shared data structure
        updatedCarPages.put(auctionEvent.getCarPageKey(), updatedCarPage);
    }

    public Page<Car> getUpdatedCarPage(User user, Page<Car> carPage) {
        //the key to search in the map for the car page
        int carPageKey = 1;

        auctionService.checkAndPublishFinishedAuctions(user, carPage, carPageKey);

        //if the map is not updated(listener was not executed then NPE might get thrown)
        Page<Car> updatedCarPage = getUpdatedCarPage(carPageKey);
        if (updatedCarPage == null) {
            return carPage;
        }

        return updatedCarPage;
    }

    public Page<Car> getUpdatedCarPage(int key) {
        return updatedCarPages.get(key);
    }
}