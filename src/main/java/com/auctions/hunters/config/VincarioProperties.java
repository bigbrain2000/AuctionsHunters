package com.auctions.hunters.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class VincarioProperties {

    @Value("${vicarioKey}")
    private String vicarioKey;

    @Value("${vicarioSecret}")
    private String vicarioSecret;

    @Value("${vicarioBaseUrl}")
    private String vicarioBaseUrl;
}

