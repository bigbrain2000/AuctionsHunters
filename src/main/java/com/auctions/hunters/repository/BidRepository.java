package com.auctions.hunters.repository;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

    /**
     * Retrieved an {@link Bid} from the database where the foreign key, user_id is equal to the parameter value.
     */
    List<Bid> findByUserId(Integer userId);

    /**
     * Retrieved a list of {@link Auction} objects from the database based on the provided {@link User} that created them.
     */
    @Query("SELECT DISTINCT b.auction FROM Bid b WHERE b.user = :user")
    List<Auction> findAuctionsByUser(@Param("user") User user);

    /**
     * Retrieved a list of {@link Bid} objects from the database that a {@link User} made for an {@link Auction}.
     */
    @Query("SELECT b FROM Bid b WHERE b.user = :user AND b.auction.id = :auctionId ORDER BY b.id DESC")
    List<Bid> findUserBidsForAuction(@Param("user") User user, @Param("auctionId") int auctionId);
}
