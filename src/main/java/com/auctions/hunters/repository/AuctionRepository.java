package com.auctions.hunters.repository;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.enums.AuctionStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    /**
     * Retrieves all auctions from the database.
     *
     * @return a list with all the auctions found
     */
    @Override
    @NotNull
    List<Auction> findAll();

    /**
     * Retrieves an {@link Auction} from the database where the foreign key, car_id is equal to the parameter value.
     */
    @Query("SELECT a FROM Auction a WHERE a.car.id = ?1")
    Auction findByCarId(@Param("carId") Integer carId);

    /**
     * Retrieves a list of {@link Auction} from the database where the foreign key, user_id is equal to the parameter value.
     */
    List<Auction> findByUserId(Integer userId);

    /**
     * Retrieves a list of {@link Auction} objects from the database where the foreign key, buyer_id is equal to the parameter value
     * and the provided {@code status} matches the {@code CLOSED} status from {@link AuctionStatus}.
     */
    List<Auction> findAllByBuyerIdAndStatus(Integer buyerId, AuctionStatus status);

    /**
     * Retrieves a list of {@link Auction} from the database where based on the cars list provided.
     */
    List<Auction> findByCarIn(List<Car> cars);
}
