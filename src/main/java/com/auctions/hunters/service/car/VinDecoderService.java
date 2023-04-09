package com.auctions.hunters.service.car;

import com.auctions.hunters.config.VincarioProperties;
import com.auctions.hunters.exceptions.CarPayloadFailedToCreateException;
import com.auctions.hunters.exceptions.NotEnoughLookupsException;
import com.auctions.hunters.exceptions.UnrecognizedVinException;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.service.car.coverter.CarPayloadConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class VinDecoderService {

    private final VincarioProperties properties;
    private final CarPayloadConverter converter;

    private static final String ACTION = "decode";

    public VinDecoderService(VincarioProperties properties,
                             CarPayloadConverter converter) {
        this.properties = properties;
        this.converter = converter;
    }

    public Car decodeVin(String vin) throws CarPayloadFailedToCreateException, NotEnoughLookupsException, UnrecognizedVinException {

        if (!isVinValid(vin)) {
            throw new UnrecognizedVinException("Provide VIN is not valid!");
        }

        try {
            String controlSum = sha1(vin + "|" + ACTION + "|" + properties.getVicardioKey() + "|" + properties.getVicardioSecret()).substring(0, 10);
            String requestUrl = properties.getVicardioBaseUrl() + "/" + properties.getVicardioKey() + "/" + controlSum + "/" + ACTION + "/" + vin + ".json";
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            log.debug("Response code for calling the decode API is {} ", responseCode);

            StringBuilder response = readDataFromConnection(connection);
            checkPotentialErrorsInJson(response);

            return converter.convertJsonToPayload(response.toString());

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new CarPayloadFailedToCreateException("Exception at calling the Vincario API!");
        }
    }

    @NotNull
    private StringBuilder readDataFromConnection(HttpURLConnection connection) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }

        bufferedReader.close();
        return response;
    }

    private void checkPotentialErrorsInJson(StringBuilder response) throws JsonProcessingException, NotEnoughLookupsException, UnrecognizedVinException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.toString());
        if (jsonNode.has("error")) {
            String errorMessage = jsonNode.get("error").asText();
            log.error("Error at making the call to verify the VIN. Trace:  {}", errorMessage);
        }

        if (jsonNode.has("error")) {
            String errorMessage = jsonNode.get("message").asText();
            log.error("Error at making the call to verify the VIN. Trace:  {}", errorMessage);

            if ("Not Enough Lookups".equals(errorMessage)) {
                throw new NotEnoughLookupsException("The number of lookups has been reached.");
            }
        }
    }

    private String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public boolean isVinValid(String vin) throws UnrecognizedVinException {

        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 0, 1, 2, 3, 4, 5, 0, 7, 0, 9,
                2, 3, 4, 5, 6, 7, 8, 9};
        int[] weights = {8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2};

        String s = vin;
        s = s.replace("-", "");
        s = s.replace(" ", "");
        s = s.toUpperCase();
        if (s.length() != 17) {
            throw new UnrecognizedVinException("Provided VIN number must have 17 characters");
        }

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = s.charAt(i);
            int value;
            int weight = weights[i];

            // letter
            if (c >= 'A' && c <= 'Z') {
                value = values[c - 'A'];
                if (value == 0)
                    throw new UnrecognizedVinException("Provided vin contains an illegal character: " + c);
            }

            // number
            else if (c >= '0' && c <= '9')
                value = c - '0';

                // illegal character
            else {
                throw new UnrecognizedVinException("Provided vin contains an illegal character: " + c);
            }

            sum = sum + weight * value;
        }

        // check digit
        sum = sum % 11;
        char check = s.charAt(8);
        if ((sum == 10 && check == 'X') || (sum == transliterate(check))) {
            log.info("Provided VIN is valid.");
            return true;
        } else {
            log.info("Provided VIN is invalid.");
            return false;
        }
    }

    private int transliterate(char check) {
        if (check == 'A' || check == 'J') {
            return 1;
        } else if (check == 'B' || check == 'K' || check == 'S') {
            return 2;
        } else if (check == 'C' || check == 'L' || check == 'T') {
            return 3;
        } else if (check == 'D' || check == 'M' || check == 'U') {
            return 4;
        } else if (check == 'E' || check == 'N' || check == 'V') {
            return 5;
        } else if (check == 'F' || check == 'W') {
            return 6;
        } else if (check == 'G' || check == 'P' || check == 'X') {
            return 7;
        } else if (check == 'H' || check == 'Y') {
            return 8;
        } else if (check == 'R' || check == 'Z') {
            return 9;
        } else if (Integer.valueOf(Character.getNumericValue(check)) != null) {
            return Character.getNumericValue(check);
        }
        return -1;
    }
}