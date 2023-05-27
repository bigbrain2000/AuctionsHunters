package com.auctions.hunters.service.car.vincario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleRecord {

    private String market;

    private int price;

    @JsonProperty("price_currency")
    private String priceCurrency;

    private int odometer;

    @JsonProperty("odometer_unit")
    private String odometerUnit;
}