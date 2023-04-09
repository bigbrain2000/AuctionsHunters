package com.auctions.hunters.exceptions;

/**
 * Exception thrown when the maximum amount of calls to the Vicardio API hax been reached.
 */
public class NotEnoughLookupsException extends Exception {

    public NotEnoughLookupsException(String message) {
        super(message);
    }
}
