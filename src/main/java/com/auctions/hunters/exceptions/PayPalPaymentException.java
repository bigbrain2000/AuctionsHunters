package com.auctions.hunters.exceptions;

import com.paypal.base.exception.PayPalException;

/**
 * Exception thrown when there is an error at completing the payment using PayPal.
 */
public class PayPalPaymentException extends PayPalException {
    public PayPalPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
