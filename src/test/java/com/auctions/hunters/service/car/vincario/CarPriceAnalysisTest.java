package com.auctions.hunters.service.car.vincario;

import com.auctions.hunters.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarPriceAnalysisTest {

    private CarPriceAnalysis carAnalyzer;

    @Mock
    private WebClient webClientMock;
    private WebClient.RequestHeadersSpec requestHeadersMock;
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    public void setup() {

        webClientMock = mock(WebClient.class);
        requestHeadersMock = mock(WebClient.RequestHeadersSpec.class);
        requestHeadersUriMock = mock(WebClient.RequestHeadersUriSpec.class);
        responseSpecMock = mock(WebClient.ResponseSpec.class);
        carAnalyzer = new CarPriceAnalysis();
        carAnalyzer.setWebClient(webClientMock);  // You will need to create a setter for this
    }

    @Test
    public void testAnalyzeCarPrice_CarNotFound() {
        // Given
        Car car = new Car();
        car.setVin("12345");
        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(anyString(), anyString(), anyString(), anyString())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just(""));

        // When
        String result = carAnalyzer.analyzeCarPrice(car);

        // Then
        assertEquals("The car could not be found", result);
    }
}