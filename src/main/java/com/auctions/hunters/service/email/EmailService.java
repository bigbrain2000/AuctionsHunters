package com.auctions.hunters.service.email;

import javax.validation.constraints.NotBlank;

/**
 * Interface used for declaring the methods signature that can process emails.
 */
public interface EmailService {

    /**
     * Method used for sending an email. Default, the sender is "auctionshunters@gmail.com".
     *
     * @param to      addressee
     * @param subject the email subject
     * @param body-   the email we want to send
     * @throws IllegalStateException in case of errors at sending the email
     */
    void sendEmail(@NotBlank String to, @NotBlank String subject, @NotBlank String body);
}
