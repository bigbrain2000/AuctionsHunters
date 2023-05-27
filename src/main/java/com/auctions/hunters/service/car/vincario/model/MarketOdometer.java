package com.auctions.hunters.service.car.vincario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketOdometer {

    @JsonProperty("odometer_count")
    private int odometerCount;

    @JsonProperty("odometer_unit")
    private String odometerUnit;

    @JsonProperty("odometer_below")
    private int odometerBelow;

    @JsonProperty("odometer_mean")
    private int odometerMean;

    @JsonProperty("odometer_avg")
    private int odometerAvg;

    @JsonProperty("odometer_above")
    private int odometerAbove;

    @JsonProperty("odometer_stdev")
    private int odometerStdev;
}

