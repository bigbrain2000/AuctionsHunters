package com.auctions.hunters.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotBlank;

/**
 * Concrete class that implements {@link EmailService}.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Method used for sending an email. Default, the sender is "auctionshunters@gmail.com".
     *
     * @param to      addressee
     * @param subject the email subject
     * @param body-   the email we want to send
     * @throws IllegalStateException in case of errors at sending the email
     */
    @Async
    @Override
    public void sendEmail(@NotBlank String to, @NotBlank String subject, @NotBlank String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(body, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("vânătoriidelicitații@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.debug("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }
    }
}