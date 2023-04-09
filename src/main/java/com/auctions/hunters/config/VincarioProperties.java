package com.auctions.hunters.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class VincarioProperties {

    @Value("${vicardioKey}")
    private String vicardioKey;

    @Value("${vicardioSecret}")
    private String vicardioSecret;

    @Value("${vicardioBaseUrl}")
    private String vicardioBaseUrl;
}

