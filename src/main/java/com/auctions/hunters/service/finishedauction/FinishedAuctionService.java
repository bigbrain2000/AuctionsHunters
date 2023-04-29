package com.auctions.hunters.service.finishedauction;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FinishedAuctionService {

    void save(List<Auction> auctions);

    void manageFinishedAuctions(User user, Page<Car> carPage);

    /**
     * Retrieves a list of {@link Car} from the database by the id of the new owner
     *
     * @param buyer The unique identifier of the buyer for whom to fetch the finished auctions.
     * @return A list of {@link Car} objects, containing all the cars won by the user identified by the id param.
     * If no cars are found for the given buyer, an empty list is returned.
     */
    List<Car> getCarsFromFinishedAuctionsForBuyer(Integer buyer);
}
