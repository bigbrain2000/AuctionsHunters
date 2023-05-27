package com.auctions.hunters.service.car.vincario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketPrice {

    @JsonProperty("price_count")
    private int priceCount;

    @JsonProperty("price_currency")
    private String priceCurrency;

    @JsonProperty("price_below")
    private int priceBelow;

    @JsonProperty("price_mean")
    private int priceMean;

    @JsonProperty("price_avg")
    private int priceAvg;

    @JsonProperty("price_above")
    private int priceAbove;

    @JsonProperty("price_stdev")
    private int priceStdev;
}
