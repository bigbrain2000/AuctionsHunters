package com.auctions.hunters.utils;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;

/**
 * Class used for defining the util methods/constants for dates.
 */
public class DateUtils {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    /**
     * Obtains the current date-time from the system clock in the default time-zone and return a OffsetDateTime having
     * the DATE_TIME_PATTERN.
     */
    @NotNull
    public static OffsetDateTime getDateTime() {
        String dateTimeString = now().toString();
        String dateTimeWithTimeZoneString = dateTimeString + "Z";
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeWithTimeZoneString);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return OffsetDateTime.parse(zonedDateTime.format(formatter));
    }
}
