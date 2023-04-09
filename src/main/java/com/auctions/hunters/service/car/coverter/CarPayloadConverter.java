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
        carInfo.setEngineDisplacement(labelValueMap.get("Engine Displacement (ccm)"));
        carInfo.setEnginePower(labelValueMap.get("Engine Power (kW)"));
        carInfo.setFuelTypePrimary(labelValueMap.get("Fuel Type - Primary"));
        carInfo.setTransmission(labelValueMap.get("Transmission"));
        carInfo.setDrive(labelValueMap.get("Drive"));
        carInfo.setNumberOfDoors(labelValueMap.get("Number of Doors"));
        carInfo.setNumberOfSeats(labelValueMap.get("Number of Seats"));
        carInfo.setLength(labelValueMap.get("Length (mm)"));
        carInfo.setHeight(labelValueMap.get("Height (mm)"));
        carInfo.setWidth(labelValueMap.get("Width (mm)"));
        carInfo.setMaxWeight(labelValueMap.get("Max Weight (kg)"));
        carInfo.setFuelConsumptionCombined(labelValueMap.get("Fuel Consumption Combined (l/100km)"));
        carInfo.setEmissionStandard(labelValueMap.get("Emission Standard"));
        return carInfo;
    }
}
