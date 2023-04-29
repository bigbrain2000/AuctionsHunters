package com.auctions.hunters.repository;

import com.auctions.hunters.model.FinishedAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinishedAuctionRepository extends JpaRepository<FinishedAuction, Integer> {

    /**
     * Retrieves a list of {@link FinishedAuction} from the database where the specified buyer has won.
     */
    List<FinishedAuction> findAllByBuyerId(Integer buyerId);
}