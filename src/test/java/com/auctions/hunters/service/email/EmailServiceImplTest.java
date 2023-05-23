package com.auctions.hunters.service.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;

    private EmailService uut;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new EmailServiceImpl(mailSender));
    }

    @Test
    void sendEmail_validInputs_isSuccess() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);

        uut.sendEmail("alex@yahoo.com", "register", "body");

        verify(mailSender, times(1)).send(mimeMessage);
    }
}