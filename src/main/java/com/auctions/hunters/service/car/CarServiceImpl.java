package com.auctions.hunters.service.car;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.service.user.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.List.of;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final SellerService sellerService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(CarServiceImpl.class);

    public CarServiceImpl(CarRepository carRepository,
                          SellerService sellerService) {
        this.carRepository = carRepository;
        this.sellerService = sellerService;
    }

    @Override
    public Car save(Car car) {
        User user = sellerService.findByUsername(sellerService.getLoggedUsername());

        Car newCar = Car.builder()
                .user(user)
                .category(car.getCategory())
                .model(car.getModel())
                .vin(car.getVin())
                .tankCapacity(car.getTankCapacity())
                .color(car.getColor())
                .manufacturingYear(car.getManufacturingYear())
                .horsePower(car.getHorsePower())
                .mileage(car.getMileage())
                .transmissionType(car.getTransmissionType())
                .fuelType(car.getFuelType())
                .pollutionStandard(car.getPollutionStandard())
                .numberOfPreviousOwners(car.getNumberOfPreviousOwners())
                .numberOfPreviousAccidents(car.getNumberOfPreviousAccidents())
                .build();

        return carRepository.save(newCar);
    }

    @Override
    public void deleteById(Integer carId) {
        Optional<Car> optionalCar = carRepository.findById(carId);

        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();
            User user = car.getUser();
            if (user != null) {
                user.getCarList().remove(car);
            }
            carRepository.delete(car);
        } else {
            throw new IllegalArgumentException("Car with ID " + carId + " does not exist.");
        }
    }

    @Override
    public List<Car> findAll() {
        List<Car> carList = carRepository.findAll();

        if (carList.isEmpty()) {
            LOGGER.debug("The cars list was empty.");
            return of();
        }

        LOGGER.debug("The cars list was retrieved from the database.");
        return new ArrayList<>(carList);
    }

}
