package com.subha.uri.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateTimeUtils {
  public static LocalDateTime convertToUtc(LocalDateTime localDateTime) {
    ZonedDateTime dateTimeInMyZone = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
    return dateTimeInMyZone.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
  }

  public static LocalDateTime convertFromUtc(LocalDateTime utcDateTime) {
    ZonedDateTime dateTimeInUtc = ZonedDateTime.of(utcDateTime, ZoneOffset.UTC);
    return dateTimeInUtc.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static String formatToClickhouseFormat(LocalDateTime localDateTime) {
    return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public static List<LocalDateTime> initialiseInterval(String startTime, String endTime) {
    LocalDateTime intervalStart, intervalEnd;
    try {
      intervalEnd = LocalDateTime.parse(endTime,
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    } catch (DateTimeParseException e) {
      intervalEnd = LocalDateTime.now();
    }
    try {
      intervalStart = LocalDateTime.parse(
          startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    } catch (DateTimeParseException e) {
      intervalStart = intervalEnd.minusDays(1);
    }
    return List.of(intervalStart, intervalEnd);
  }

  public static List<LocalDateTime> initialiseTimestamp(String startTime, String endTime) {
    LocalDateTime intervalStart, intervalEnd;
    try {
      intervalEnd = LocalDateTime.parse(endTime,
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    } catch (DateTimeParseException e) {
      intervalEnd = LocalDateTime.now();
    }
    try {
      intervalStart = LocalDateTime.parse(
          startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    } catch (DateTimeParseException e) {
      intervalStart = intervalEnd.minusYears(20);
    }
    return List.of(intervalStart, intervalEnd);
  }
}
