package com.auctions.hunters.repository;

import com.auctions.hunters.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

    @Query("SELECT b FROM Bid b JOIN FETCH b.user WHERE b.auction.id = :auctionId")
    List<Bid> findByAuctionId(@Param("auctionId") Integer auctionId);

    List<Bid> findByUserId(Integer userId);

}
