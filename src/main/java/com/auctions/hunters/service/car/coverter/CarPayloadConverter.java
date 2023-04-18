package com.auctions.hunters.service.car.coverter;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.car.model.KeyValuePair;
import com.auctions.hunters.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CarPayloadConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    public CarPayloadConverter(UserService userService) {
        this.userService = userService;
    }

    /**
     * Converts a JSON String containing a list of {@link KeyValuePair} objects
     * into a {@link Car} object with fields populated from the {@link KeyValuePair} data.
     *
     * @param jsonString the input JSON String
     * @return a {@link Car} object with fields populated from the {@link KeyValuePair} data.
     */
    public Car convertJsonToPayload(String jsonString) throws IOException {
        try {
            Map<String, String> labelValueMap = getKeyValueMapFromResponse(jsonString);

            return setCarFields(labelValueMap);
        } catch (IOException e) {
            log.error("Failed to map JSON to Java object.");
            throw new IOException(jsonString, e);
        }
    }

    @NotNull
    private Map<String, String> getKeyValueMapFromResponse(String jsonString) throws JsonProcessingException {
        //read the JSON as a JsonNode object
        JsonNode rootNode = objectMapper.readTree(jsonString);

        //get the 'decode' array from the JsonNode
        JsonNode decodeArray = rootNode.get("decode");

        //create a map to store the key-value pairs
        Map<String, String> labelValueMap = new HashMap<>();

        // Iterate through the array elements and populate the map
        for (JsonNode element : decodeArray) {
            String label = element.get("label").asText();
            String value = element.get("value").asText();
            labelValueMap.put(label, value);
        }
        return labelValueMap;
    }

    @NotNull
    private Car setCarFields(Map<String, String> labelValueMap) {
        //search for the logged user
        User user = userService.findByUsername(userService.getLoggedUsername());

        //create a car object and set his fields
        Car carInfo = new Car();
        carInfo.setUser(user);
        carInfo.setVin(labelValueMap.get("VIN"));
        carInfo.setProducer(labelValueMap.get("Make"));
        carInfo.setModel(labelValueMap.get("Model"));
        carInfo.setModelYear(labelValueMap.get("Model Year"));
        carInfo.setBody(labelValueMap.get("Body"));
        carInfo.setSeries(labelValueMap.get("Series"));
        carInfo.setDrive(labelValueMap.get("Drive"));
        carInfo.setEngineDisplacement(labelValueMap.get("Engine Displacement (ccm)"));
        carInfo.setEnginePower(labelValueMap.get("Engine Power (HP)"));
        carInfo.setFuelTypePrimary(labelValueMap.get("Fuel Type - Primary"));
        carInfo.setEngineCode(labelValueMap.get("Engine Code"));
        carInfo.setTransmission(labelValueMap.get("Transmission"));
        carInfo.setNumberOfGears(labelValueMap.get("Number of Gears"));
        carInfo.setEmissionStandard(labelValueMap.get("Emission Standard"));
        carInfo.setManufacturerAddress(labelValueMap.get("Manufacturer Address"));
        carInfo.setManufacturerCountry(labelValueMap.get("Plant Country"));
        carInfo.setEngineRpm(labelValueMap.get("Engine RPM"));
        carInfo.setNumberOfCylinders(labelValueMap.get("Engine Type"));
        carInfo.setFuelConsumptionCombined(labelValueMap.get("Fuel Consumption Combined (l/100km)"));
        carInfo.setFuelConsumptionUrban(labelValueMap.get("Fuel Consumption Urban (l/100km)"));
        carInfo.setCo2Emission(labelValueMap.get("CO2 Emission (g/km)"));
        carInfo.setAxleRatio(labelValueMap.get("Axle Ratio"));
        carInfo.setNumberOfWheels(labelValueMap.get("Number Wheels"));
        carInfo.setNumberOfAxles(labelValueMap.get("Number of Axles"));
        carInfo.setNumberOfDoors(labelValueMap.get("Number of Doors"));
        carInfo.setNumberOfSeats(labelValueMap.get("Number of Seats"));
        carInfo.setFrontBrakes(labelValueMap.get("Front Brakes"));
        carInfo.setBrakeSystem(labelValueMap.get("Brake System"));
        carInfo.setSuspension(labelValueMap.get("Suspension"));
        carInfo.setSteeringType(labelValueMap.get("Steering Type"));
        carInfo.setWheelSize(labelValueMap.get("Wheel Size"));
        carInfo.setWheelBase(labelValueMap.get("Wheelbase (mm)"));
        carInfo.setHeight(labelValueMap.get("Height (mm)"));
        carInfo.setLength(labelValueMap.get("Length (mm)"));
        carInfo.setWidth(labelValueMap.get("Width (mm)"));
        carInfo.setMaxSpeed(labelValueMap.get("Max Speed (km/h)"));
        carInfo.setEmptyWeight(labelValueMap.get("Weight Empty (kg)"));
        carInfo.setMaxWeight(labelValueMap.get("Max Weight (kg)"));
        carInfo.setMaxRoofLoad(labelValueMap.get("Max roof load (kg)"));
        carInfo.setAbs(labelValueMap.get("ABS"));

        return carInfo;
    }
}
