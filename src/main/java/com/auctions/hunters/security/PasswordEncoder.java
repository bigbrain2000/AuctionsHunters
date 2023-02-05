package com.auctions.hunters.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Class used for defining the {@link BCryptPasswordEncoder} bea.
 */
@Configuration
public class PasswordEncoder {

    /**
     * Implementation of {@link BCryptPasswordEncoder} that uses the BCrypt strong hashing function.
     * The version that the hashing method uses is "$2a" and the strength (a.k.a. log rounds in BCrypt) value is 10.
     *
     * @return the hashed password
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
