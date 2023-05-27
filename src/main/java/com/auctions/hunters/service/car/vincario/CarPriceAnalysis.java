package com.auctions.hunters.service.car.vincario;

import com.auctions.hunters.config.VincarioProperties;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.car.vincario.model.MarketOdometer;
import com.auctions.hunters.service.car.vincario.model.PriceAnalysisResponse;
import com.auctions.hunters.service.car.vincario.model.VehicleRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;

import static com.auctions.hunters.service.car.vincario.EncryptUtils.sha1;

@Slf4j
@Component
public class CarPriceAnalysis {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ACTION = "vehicle-market-value";

    private final VincarioProperties properties;

    public CarPriceAnalysis(VincarioProperties properties) {
        this.properties = properties;
    }

    public String analyzeCarPrice(@NotNull Car car) throws NoSuchAlgorithmException {
        String controlsum = sha1(car.getVin() + "|" + ACTION + "|" + properties.getVicarioKey() + "|" + properties.getVicarioSecret()).substring(0, 10);
        WebClient client = WebClient.create(properties.getVicarioBaseUrl());

        String response = client.get()
                .uri("/{apikey}/{controlsum}/vehicle-market-value/{vin}.json", properties.getVicarioKey(), controlsum, car.getVin())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            return "The car could not be found";
        }

        PriceAnalysisResponse carToBeAnalyzed;
        try {
            carToBeAnalyzed = OBJECT_MAPPER.readValue(response, PriceAnalysisResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (carToBeAnalyzed != null && carToBeAnalyzed.getRecords() != null) {
            int closestOdometerDiff = Integer.MAX_VALUE;
            VehicleRecord closestRecord = null;
            MarketOdometer marketOdometer = new MarketOdometer();

            for (VehicleRecord vehicleRecord : carToBeAnalyzed.getRecords()) {
                int odometerDiff = Math.abs(vehicleRecord.getOdometer() - marketOdometer.getOdometerAvg());
                if (odometerDiff < closestOdometerDiff) {
                    closestOdometerDiff = odometerDiff;
                    closestRecord = vehicleRecord;
                }
            }

            if (closestRecord == null) {
                log.error("The car value could not be calculated.");
                return null;
            }

            return String.valueOf(closestRecord.getPrice());

        } else {
            log.error("The car or Vehicle Records is null");
            return null;
        }
    }
}
