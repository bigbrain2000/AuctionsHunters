package com.auctions.hunters.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class PayPalProperties {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode; //change to "live" for production

    @Value("${paypal.success.url}")
    private String successUrl;

    @Value("${paypal.cancel.url}")
    private String cancelUrl;

    @Value("${paypal.intent}")
    private String intent;

    @Value("${paypal.method}")
    private String method;

    @Value("${paypal.currency}")
    private String currency;
}
