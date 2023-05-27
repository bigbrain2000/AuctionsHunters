package com.auctions.hunters.service.car.vincario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceAnalysisResponse {

    private String vin;

    private Vehicle vehicle;

    @JsonProperty("market_price")
    private MarketPrice marketPrice;

    @JsonProperty("market_odometer")
    private MarketOdometer marketOdometer;

    private VehicleRecord[] records;
}
