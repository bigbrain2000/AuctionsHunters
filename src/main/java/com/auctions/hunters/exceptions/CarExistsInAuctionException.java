package com.auctions.hunters.exceptions;

/**
 * Exception thrown when a car is already in an auction.
 */
public class CarExistsInAuctionException extends Exception {

    public CarExistsInAuctionException(String message) {
        super(message);
    }
}
