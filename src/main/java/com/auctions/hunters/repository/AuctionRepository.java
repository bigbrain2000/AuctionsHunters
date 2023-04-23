package com.auctions.hunters.repository;

import com.auctions.hunters.model.Auction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    /**
     * Retrieve all auctions from the database.
     *
     * @return a list with all the auctions found
     */
    @Override
    @NotNull
    List<Auction> findAll();

    /**
     * Retrieved an {@link Auction} from the database where the foreign key, car_id is equal to the parameter value.
     */
    @Query("SELECT a FROM Auction a WHERE a.car.id = ?1")
    @Transactional
    Auction findByCarId(@Param("carId") Integer carId);

    /**
     * Retrieved an {@link Auction} from the database where the foreign key, user_id is equal to the parameter value.
     */
    @Transactional
    List<Auction> findByUserId(Integer userId);
}
