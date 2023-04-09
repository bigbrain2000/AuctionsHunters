package com.auctions.hunters.exceptions;

/**
 * Exception thrown when a car`s VIN already exists in the database.
 */
public class CarVinAlreadyExistsException extends Exception {

    public CarVinAlreadyExistsException(String message) {
        super(message);
    }
}
