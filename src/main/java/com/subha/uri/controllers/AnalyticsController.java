package com.subha.uri.controllers;

import com.subha.uri.domain.entity.Url;
import com.subha.uri.domain.entity.User;
import com.subha.uri.repository.postgres.UrlRepository;
import com.subha.uri.services.EventService;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UrlService;
import com.subha.uri.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/analytics/")
public class AnalyticsController {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UrlRepository urlRepository;

  @Autowired
  private UrlService urlService;

  @Autowired
  private EventService eventService;

  @GetMapping("/country/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByCountry(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
    if (urlFound.isEmpty()) {
      return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
    }

    Url url = urlFound.get();
    User urlUser = url.getUser();
    if (!Objects.equals(urlUser.getId(), userId)) {
      return new ResponseEntity<>(
          Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
    }

    List<LocalDateTime> interval = DateTimeUtils.initialiseTimestamp(startTime, endTime);
    LocalDateTime intervalStart = interval.get(0);
    LocalDateTime intervalEnd = interval.get(1);

    if (intervalStart.isAfter(intervalEnd)) {
      return new ResponseEntity<>(
          Map.of("message", "Invalid time interval"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Long> clickByCountry = eventService.getClickByCountry(shortUrl, intervalStart,
        intervalEnd);
    return ResponseEntity.ok(clickByCountry);
  }

  @GetMapping("/ip/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByIpAddress(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
    if (urlFound.isEmpty()) {
      return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
    }

    Url url = urlFound.get();
    User urlUser = url.getUser();
    if (!Objects.equals(urlUser.getId(), userId)) {
      return new ResponseEntity<>(
          Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
    }

    List<LocalDateTime> interval = DateTimeUtils.initialiseTimestamp(startTime, endTime);
    LocalDateTime intervalStart = interval.get(0);
    LocalDateTime intervalEnd = interval.get(1);

    if (intervalStart.isAfter(intervalEnd)) {
      return new ResponseEntity<>(
          Map.of("message", "Invalid time interval"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Long> clickByCountry = eventService.getEventByIpAddress(shortUrl, intervalStart,
        intervalEnd);
    return ResponseEntity.ok(clickByCountry);
  }

  @GetMapping("/referrer/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByReferrer(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
    if (urlFound.isEmpty()) {
      return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
    }

    Url url = urlFound.get();
    User urlUser = url.getUser();
    if (!Objects.equals(urlUser.getId(), userId)) {
      return new ResponseEntity<>(
          Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
    }

    List<LocalDateTime> interval = DateTimeUtils.initialiseTimestamp(startTime, endTime);
    LocalDateTime intervalStart = interval.get(0);
    LocalDateTime intervalEnd = interval.get(1);

    if (intervalStart.isAfter(intervalEnd)) {
      return new ResponseEntity<>(
          Map.of("message", "Invalid time interval"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Long> clickByCountry = eventService.getEventByReferrer(shortUrl, intervalStart,
        intervalEnd);
    return ResponseEntity.ok(clickByCountry);
  }

  @GetMapping("/user-agent/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByUserAgent(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
    if (urlFound.isEmpty()) {
      return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
    }

    Url url = urlFound.get();
    User urlUser = url.getUser();
    if (!Objects.equals(urlUser.getId(), userId)) {
      return new ResponseEntity<>(
          Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
    }

    List<LocalDateTime> interval = DateTimeUtils.initialiseTimestamp(startTime, endTime);
    LocalDateTime intervalStart = interval.get(0);
    LocalDateTime intervalEnd = interval.get(1);

    if (intervalStart.isAfter(intervalEnd)) {
      return new ResponseEntity<>(
          Map.of("message", "Invalid time interval"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Long> clickByCountry = eventService.getEventByUserAgent(shortUrl, intervalStart,
        intervalEnd);
    return ResponseEntity.ok(clickByCountry);
  }

  @GetMapping("/click/{shortUrl}")
  public ResponseEntity<Object> getTotalClickByInterval(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
    if (urlFound.isEmpty()) {
      return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
    }

    Url url = urlFound.get();
    User urlUser = url.getUser();
    if (!Objects.equals(urlUser.getId(), userId)) {
      return new ResponseEntity<>(
          Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
    }

    List<LocalDateTime> interval = DateTimeUtils.initialiseInterval(startTime, endTime);
    LocalDateTime intervalStart = interval.get(0);
    LocalDateTime intervalEnd = interval.get(1);

    if (intervalStart.isAfter(intervalEnd)) {
      return new ResponseEntity<>(
          Map.of("message", "Invalid time interval"), HttpStatus.BAD_REQUEST);
    }

    Map<String, Long> clickLastDay = eventService.getClickByInterval(shortUrl, intervalStart,
        intervalEnd);
    return ResponseEntity.ok(clickLastDay);
  }

  @GetMapping("/click/{shortUrl}/all")
  public ResponseEntity<Object> getTotalClickByShortUrl(
      @PathVariable("shortUrl") String shortUrl,
      @RequestHeader("Authorization") String bearerToken) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
    if (urlFound.isEmpty()) {
      return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
    }
    Url url = urlFound.get();
    User urlUser = url.getUser();
    if (!Objects.equals(urlUser.getId(), userId)) {
      return new ResponseEntity<>(
          Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
    }
    Map<String, Long> totalClicks = eventService.getTotalClick(shortUrl);
    return ResponseEntity.ok(totalClicks);
  }
}
