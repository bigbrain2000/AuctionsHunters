package com.auctions.hunters.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class VehicleMarketValueService {

    private final WebClient webClient;

    private static final String API_PREFIX = "https://api.vindecoder.eu/3.2";
    private static final String API_KEY = "";
    private static final String SECRET_KEY = "";
    private static final String ID = "vehicle-market-value";

    @Autowired
    public VehicleMarketValueService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(API_PREFIX).build();
    }

    public Mono<String> getVehicleMarketValue(String vin) {
        String controlSum = calculateControlSum(vin);

        return webClient.get()
                .uri("/{apiKey}/{controlSum}/vehicle-market-value/{vin}.json", API_KEY, controlSum, vin.toUpperCase())
                .retrieve()
                .bodyToMono(String.class);
    }

    private String calculateControlSum(String vin) {
        String data = String.format("%s|%s|%s|%s", vin, ID, API_KEY, SECRET_KEY);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString().substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to calculate control sum", e);
        }
    }
}
