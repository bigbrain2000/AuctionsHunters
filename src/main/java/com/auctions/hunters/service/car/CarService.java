package com.auctions.hunters.service.car;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.enums.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Car} entity.
 */
public interface CarService {

    /**
     * Saves a car in the database for the logged user.
     *
     * @param vin the car`s VIN
     */
    Car save(@NotBlank String vin) throws NotEnoughLookupsException, CarPayloadFailedToCreateException, UnrecognizedVinException, CarVinAlreadyExistsException;

    /**
     * Delete a car by a specific id if the id is found in the database.
     *
     * @param carId persisted car id
     * @throws IllegalArgumentException if the car with the given id can not be found in the database
     */
    void deleteById(@NotNull Integer carId) throws CarExistsInAuctionException;

    /**
     * Returns a list with all the cars persisted in the database.
     *
     * @return a list with all the cars / empty if the list has 0 cars
     */
    List<Car> findAll();

    /**
     * Get a car by it`s id
     *
     * @return the found car
     */
    Car getCarById(@NotNull Integer carId);

    /**
     * Update the status of a car if it`s being auctioned.
     *
     * @param id            persisted car id
     * @param auctionStatus the status of the car. False means it`s not being auctioned and true otherwise.
     * @return the new updated car that`s being saved in the database
     */
    Car updateCarAuctionStatus(@NotNull Integer id, @NotNull CarStatus auctionStatus);

    /**
     * Retrieve a set car page with 10 cars.
     */
    Page<Car> getCarPage(@NotNull int page, @NotNull Specification<Car> spec);

    /**
     * Returns all the cars that belong to the authenticated user.
     *
     * @return a list with all the cars that belong to the authenticated user, an empty list otherwise
     */
    List<Car> getAuthenticatedUserCarsList();

    /**
     * Retrieve a list of {@link Car} objects from the database that match the given id list.
     */
    List<Car> findAllByIdIn(@NotNull List<Integer> carsIdList);

    /**
     * Returns a list with all the SUV cars from the database.
     *
     * @return a list with all the cars / empty if the list has 0 cars found
     */
    List<Car> getAllCarsByBodyType(String bodyType);
}
