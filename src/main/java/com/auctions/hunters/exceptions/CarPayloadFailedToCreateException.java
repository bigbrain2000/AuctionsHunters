package com.auctions.hunters.exceptions;

/**
 * Exception thrown when a car is already in an auction.
 */
public class CarPayloadFailedToCreateException extends Exception {

    public CarPayloadFailedToCreateException(String message) {
        super(message);
    }
}
