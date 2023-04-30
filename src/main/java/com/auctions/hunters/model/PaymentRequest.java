package com.auctions.hunters.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull
    private double totalAmount;
}