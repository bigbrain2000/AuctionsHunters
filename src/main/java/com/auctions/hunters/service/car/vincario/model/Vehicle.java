package com.auctions.hunters.service.car.vincario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {

    @JsonProperty("vehicle_id")
    private int vehicleId;

    private String make;

    @JsonProperty("make_id")
    private int makeId;

    private String model;

    @JsonProperty("model_id")
    private int modelId;

    @JsonProperty("model_year")
    private int modelYear;

    @JsonProperty("fuel_type_primary")
    private String fuelTypePrimary;

    @JsonProperty("fuel_type_primary_id")
    private int fuelTypePrimaryId;
}