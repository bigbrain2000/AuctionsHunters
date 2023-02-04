package com.auctions.hunters.exceptions;

/**
 * Exception thrown when the username enters an already exists in the database.
 */
public class EmailAlreadyExistsException extends Exception {

    public EmailAlreadyExistsException(String message) {
        super(String.format("Email %s already exists!", message));
    }
}
