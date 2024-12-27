package com.subha.uri.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static LocalDateTime convertToUtc(LocalDateTime localDateTime) {
        ZonedDateTime dateTimeInMyZone = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        return dateTimeInMyZone.withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public static LocalDateTime convertFromUtc(LocalDateTime utcDateTime) {
        ZonedDateTime dateTimeInUtc = ZonedDateTime.of(utcDateTime, ZoneOffset.UTC);
        return dateTimeInUtc.withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String formatToClickhouseFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
