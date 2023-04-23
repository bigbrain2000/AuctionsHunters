package com.auctions.hunters.repository;

import com.auctions.hunters.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {


    /**
     * Retrieved an {@link Bid} from the database where the foreign key, user_id is equal to the parameter value.
     */
    List<Bid> findByUserId(Integer userId);
}
