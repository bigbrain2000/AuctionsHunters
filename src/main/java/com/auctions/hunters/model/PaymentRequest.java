package com.auctions.hunters.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@Builder
public class PaymentRequest {

    @NotNull
    private double totalAmount;
//
//    @NotBlank
//    private String currency;
//
//    @NotBlank
//    private String description;
}