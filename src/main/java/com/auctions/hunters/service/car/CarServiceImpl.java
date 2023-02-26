package com.auctions.hunters.service.car;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.CarRepository;
import com.auctions.hunters.service.user.SellerService;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final SellerService sellerService;

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

}
