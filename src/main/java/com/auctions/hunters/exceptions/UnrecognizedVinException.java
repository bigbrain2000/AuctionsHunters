package com.auctions.hunters.exceptions;

/**
 * Exception thrown when an introduced VIN is not valid.
 */
public class UnrecognizedVinException extends Exception {

    public UnrecognizedVinException(String message) {
        super(message);
    }
}
