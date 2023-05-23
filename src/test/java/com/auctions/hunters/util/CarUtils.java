package com.auctions.hunters.util;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.Image;
import com.auctions.hunters.model.User;
import com.auctions.hunters.model.enums.CarStatus;

import java.util.List;

import static org.mockito.Mockito.mock;

public interface CarUtils {

    default Car mockCar() {
        User user = mock(User.class);
        Image image = mock(Image.class);

        Car carInfo = new Car();
        carInfo.setUser(user);
        carInfo.setImages(List.of(image));
        carInfo.setVin("VIN");
        carInfo.setProducer("Make");
        carInfo.setModel("Model");
        carInfo.setModelYear("Model Year");
        carInfo.setBody("Body");
        carInfo.setSeries("Series");
        carInfo.setDrive("Drive");
        carInfo.setEngineDisplacement("Engine Displacement (ccm)");
        carInfo.setEnginePower("Engine Power (HP)");
        carInfo.setFuelTypePrimary("Fuel Type - Primary");
        carInfo.setEngineCode("Engine Code");
        carInfo.setTransmission("Transmission");
        carInfo.setNumberOfGears("Number of Gears");
        carInfo.setEmissionStandard("Emission Standard");
        carInfo.setManufacturerAddress("Manufacturer Address");
        carInfo.setManufacturerCountry("Plant Country");
        carInfo.setEngineRpm("Engine RPM");
        carInfo.setNumberOfCylinders("Engine Type");
        carInfo.setFuelConsumptionCombined("Fuel Consumption Combined (l/100km)");
        carInfo.setFuelConsumptionUrban("Fuel Consumption Urban (l/100km)");
        carInfo.setCo2Emission("CO2 Emission (g/km)");
        carInfo.setAxleRatio("Axle Ratio");
        carInfo.setNumberOfWheels("Number Wheels");
        carInfo.setNumberOfAxles("Number of Axles");
        carInfo.setNumberOfDoors("Number of Doors");
        carInfo.setNumberOfSeats("Number of Seats");
        carInfo.setFrontBrakes("Front Brakes");
        carInfo.setBrakeSystem("Brake System");
        carInfo.setSuspension("Suspension");
        carInfo.setSteeringType("Steering Type");
        carInfo.setWheelSize("Wheel Size");
        carInfo.setWheelBase("Wheelbase (mm)");
        carInfo.setHeight("Height (mm)");
        carInfo.setLength("Length (mm)");
        carInfo.setWidth("Width (mm)");
        carInfo.setMaxSpeed("Max Speed (km/h)");
        carInfo.setEmptyWeight("Weight Empty (kg)");
        carInfo.setMaxWeight("Max Weight (kg)");
        carInfo.setMaxRoofLoad("Max roof load (kg)");
        carInfo.setAbs("ABS");
        carInfo.setStatus(CarStatus.AUCTIONED);

        return carInfo;
    }
}
