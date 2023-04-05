package com.auctions.hunters.service.car;

import com.auctions.hunters.model.Car;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Car} entity.
 */
@Validated
public interface CarService {

    /**
     * Save a role in the DB.
     *
     * @param car -  the car to be saved in the DB
     * @return - the saved tole
     */
    Car save(@Valid Car car);

    /**
     * Delete a car by a specific id
     *
     * @param id persisted car id
     */
    void deleteById(@NotNull Integer id);

    /**
     * Get a list with all the cars
     *
     * @return a list with all the cars
     */
    List<Car> findAll();

    /**
     * Get a car by it`s id
     *
     * @return the found car
     */
    Car getCarById(Integer carId);
}
