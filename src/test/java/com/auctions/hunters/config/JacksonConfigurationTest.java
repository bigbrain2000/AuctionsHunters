package com.auctions.hunters.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class JacksonConfigurationTest {

    @InjectMocks
    private JacksonConfiguration uut;

    @Test
    void testObjectMapper() {
        ObjectMapper objectMapper = uut.objectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        assertNotNull(objectMapper);
        assertNotNull(objectMapper.findAndRegisterModules());
    }
}