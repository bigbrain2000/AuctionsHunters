package com.auctions.hunters.service.car;

import com.auctions.hunters.model.Car;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

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
}
