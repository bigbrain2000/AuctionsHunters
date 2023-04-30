package com.auctions.hunters.repository;

import com.auctions.hunters.model.Car;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {

    /**
     * Retrieve all cars from the database.
     *
     * @return a list with all the cars found
     */
    @Override
    @NotNull
    List<Car> findAll();

    /**
     * Retrieved an Optional of {@link Car} from the database based on the VIN parameter.
     */
    @Query("SELECT car from Car car Where car.vin = :vin")
    Optional<Car> findCarByVin(@Param("vin") String vin);

    /**
     * Retrieved a list of {@link Car} objects from the database where the foreign key, user_id is equal to the parameter value.
     */
    @Transactional
    List<Car> findByUserId(Integer userId);

    List<Car> findAllByIdIn(List<Integer> carsIdList);
}
