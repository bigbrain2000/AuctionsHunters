package com.auctions.hunters.repository;

import com.auctions.hunters.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> , JpaSpecificationExecutor<Car>{

    @Override
    List<Car> findAll();

    /**
     * This query retrieves a {@link Car} based on the vin parameter.
     */
    @Query("SELECT car from Car car Where car.vin = :vin")
    Optional<Car> findCarByVin(@Param("vin") String vin);
}
