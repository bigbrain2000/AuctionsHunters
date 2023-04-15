package com.auctions.hunters.service.car;

import com.auctions.hunters.exceptions.*;
import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserService userService;
    private final VinDecoderService vinDecoderService;

    public CarServiceImpl(CarRepository carRepository,
                          UserService userService,
                          VinDecoderService vinDecoderService) {
        this.carRepository = carRepository;
        this.userService = userService;
        this.vinDecoderService = vinDecoderService;
    }

    /**
     * Saves a car in the database for the logged user.
     *
     * @param vin the car`s VIN
     */
    @Override
    public Car save(String vin) throws NotEnoughLookupsException, CarPayloadFailedToCreateException, UnrecognizedVinException, CarVinAlreadyExistsException {

        if (isCarAlreadySaved(vin)) {
            throw new CarVinAlreadyExistsException(String.format("Car with VIN %s already exists.", vin));
        }

        Car car = vinDecoderService.decodeVin(vin);

        return carRepository.save(car);
    }

    /**
     * Delete a car by a specific id if the id is found in the database.
     *
     * @param carId persisted car id
     * @throws IllegalArgumentException if the car with the given id can not be found in the database
     */
    @Override
    public void deleteById(Integer carId) throws CarExistsInAuctionException {
        Optional<Car> optionalCar = carRepository.findById(carId);

        if (optionalCar.isPresent()) {
            //delete the found car only if the car is not being auctioned
            if (optionalCar.get().getIsAuctioned().equals(false)) {
                Car car = optionalCar.get();

                removeCarFromUserList(car);

                // Remove the car from the database
                carRepository.delete(car);
                log.debug("The car with id {} has been removed from the database.", car.getId());
            } else {
                throw new CarExistsInAuctionException(String.format("Car with id %s already exists in an auction", carId));
            }
        } else {
            throw new IllegalArgumentException("Car with ID " + carId + " does not exist.");
        }
    }

    /**
     * Returns a list with all the cars persisted in the database.
     *
     * @return a list with all the cars / empty if the list has 0 cars
     */
    @Override
    public List<Car> findAll() {
        List<Car> carList = carRepository.findAll();

        if (carList.isEmpty()) {
            log.debug("The cars list was empty.");
            return of();
        }

        log.debug("The cars list was retrieved from the database.");
        return new ArrayList<>(carList);
    }

    @Override
    public Car getCarById(Integer carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);

        if (optionalCar.isPresent()) {
            log.debug("Successfully retrieved car with id {}", optionalCar.get().getId());
            return optionalCar.get();
        } else {
            throw new IllegalArgumentException("Car with ID " + carId + " does not exist.");
        }
    }

    /**
     * Query the database to see if the car`s VIN already exists for a record.
     *
     * @param vin the car`s VIN
     * @return true if a car with the given VIN is present in the database, false otherwise
     */
    public boolean isCarAlreadySaved(String vin) {
        Optional<Car> optionalCar = carRepository.findCarByVin(vin);

        if (optionalCar.isPresent()) {
            log.debug("A car already exists with id {} and vin {}.", optionalCar.get().getId(), optionalCar.get().getVin());
            return true;
        }

        log.debug("A car with vin {} does not exists.", vin);
        return false;
    }

    /**
     * Update the status of a car if it`s being auctioned.
     *
     * @param id            persisted car id
     * @param auctionStatus the status of the car. False means it`s not being auctioned and true otherwise.
     * @return the new updated car that`s being saved in the database
     */
    public Car updateCarAuctionStatus(Integer id, boolean auctionStatus) {
        String errorMessage = String.format("Car with the id: %s was not found!", id);

        Car car = carRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(errorMessage));
        log.debug("Successfully retrieved car with id {}", car.getId());

        // update the car auction state
        car.setIsAuctioned(auctionStatus);

        // Save the updated car back to the database
        return carRepository.save(car);
    }

    /**
     * Remove the provided car from the user list.
     *
     * @param car the car that will be deleted
     */
    private void removeCarFromUserList(Car car) {
        User user = car.getUser();
        if (user != null) {
            user.getCarList().remove(car);
            userService.update(user, user.getUsername()); // update the user
        }
    }
}
