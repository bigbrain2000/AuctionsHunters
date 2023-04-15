package com.auctions.hunters.repository;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    @Override
    @NotNull
    List<Auction> findAll();

    //todo:update the description

    /**
     * This query that retrieves a {@link User} entity from the database where the email field is equal to the value of the "username" parameter.
     */
    @Query("SELECT a FROM Auction a WHERE a.car.id = ?1")
    Auction findByCarId(@Param("carId") Integer carId);
}
