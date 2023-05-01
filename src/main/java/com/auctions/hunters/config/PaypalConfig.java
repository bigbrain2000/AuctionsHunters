package com.auctions.hunters.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

    private final PayPalProperties payPalProperties;

    public PaypalConfig(PayPalProperties payPalProperties) {
        this.payPalProperties = payPalProperties;
    }

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", payPalProperties.getMode());
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(payPalProperties.getClientId(), payPalProperties.getClientSecret(), paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() {
        return new APIContext(payPalProperties.getClientId(), payPalProperties.getClientSecret(),
                payPalProperties.getMode(), paypalSdkConfig());
    }
}
