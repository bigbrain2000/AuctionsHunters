package com.auctions.hunters.exceptions;

/**
 * Exception thrown when the new bid made by a user in an auction is lower than the current auction price.
 */
public class LowBidAmountException extends Exception {

    public LowBidAmountException(String message) {
        super(message);
    }
}
