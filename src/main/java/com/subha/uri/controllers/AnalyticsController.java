package com.subha.uri.controllers;

import com.subha.uri.domain.entity.Url;
import com.subha.uri.domain.entity.User;
import com.subha.uri.repository.postgres.UrlRepository;
import com.subha.uri.services.EventService;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UrlService;
import com.subha.uri.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Tag(name = "Analytics", description = "Analytics API")
@RestController
@RequestMapping(value = "/analytics/", produces = "application/json")
public class AnalyticsController {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UrlRepository urlRepository;

  @Autowired
  private UrlService urlService;

  @Autowired
  private EventService eventService;

  @Operation(summary = "Get total events by country grouped by interval")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Total events by country", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid time interval"),
      @ApiResponse(responseCode = "401", description = "Unauthorised Access. Url Owner mismatch"),
      @ApiResponse(responseCode = "404", description = "Url not found")})
  @GetMapping("/country/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByCountry(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @Parameter(description = "Start Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @Parameter(description = "End Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
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

  @Operation(summary = "Get total events by ip address grouped by interval")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Total events by ip address", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid time interval"),
      @ApiResponse(responseCode = "401", description = "Unauthorised Access. Url Owner mismatch"),
      @ApiResponse(responseCode = "404", description = "Url not found")})
  @GetMapping("/ip/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByIpAddress(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @Parameter(description = "Start Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @Parameter(description = "End Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
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

  @Operation(summary = "Get total events by referrer grouped by interval")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Total events by referrer", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid time interval"),
      @ApiResponse(responseCode = "401", description = "Unauthorised Access. Url Owner mismatch"),
      @ApiResponse(responseCode = "404", description = "Url not found")})
  @GetMapping("/referrer/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByReferrer(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @Parameter(description = "Start Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @Parameter(description = "End Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
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

  @Operation(summary = "Get total events by user agent grouped by interval")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Total events by user agent", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid time interval"),
      @ApiResponse(responseCode = "401", description = "Unauthorised Access. Url Owner mismatch"),
      @ApiResponse(responseCode = "404", description = "Url not found")})
  @GetMapping("/user-agent/{shortUrl}")
  public ResponseEntity<Object> getTotalEventsByUserAgent(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @Parameter(description = "Start Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @Parameter(description = "End Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
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

  @Operation(summary = "Get total events by interval grouped by interval")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Total events by interval", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid time interval"),
      @ApiResponse(responseCode = "401", description = "Unauthorised Access. Url Owner mismatch"),
      @ApiResponse(responseCode = "404", description = "Url not found")})
  @GetMapping("/click/{shortUrl}")
  public ResponseEntity<Object> getTotalClickByInterval(
      @PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken,
      @Parameter(description = "Start Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
      @Parameter(description = "End Time in YYYY-MM-DD HH:MM:SS format", required = false) @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
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

//  @GetMapping("/click/{shortUrl}/all")
//  public ResponseEntity<Object> getTotalClickByShortUrl(
//      @PathVariable("shortUrl") String shortUrl,
//      @RequestHeader("Authorization") String bearerToken) {
//    Long userId = jwtService.extractId(bearerToken.substring(7));
//    Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
//    if (urlFound.isEmpty()) {
//      return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
//    }
//    Url url = urlFound.get();
//    User urlUser = url.getUser();
//    if (!Objects.equals(urlUser.getId(), userId)) {
//      return new ResponseEntity<>(
//          Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
//    }
//    Map<String, Long> totalClicks = eventService.getTotalClick(shortUrl);
//    return ResponseEntity.ok(totalClicks);
//  }
}
