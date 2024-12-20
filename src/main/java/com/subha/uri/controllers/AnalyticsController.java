package com.subha.uri.controllers;

import com.subha.uri.domain.entities.Url;
import com.subha.uri.domain.entities.User;
import com.subha.uri.repository.UrlRepository;
import com.subha.uri.services.EventService;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    public ResponseEntity<Object> getTotalEventsByCountry(@PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken) {
        Long userId = jwtService.extractId(bearerToken.substring(7));
        Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
        if (urlFound.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
        }
        Url url = urlFound.get();
        User urlUser = url.getUser();
        if (!Objects.equals(urlUser.getId(), userId)) {
            return new ResponseEntity<>(Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
        }
        Map<String, Long> clickByCountry = eventService.getClickByCountry(shortUrl);
        return ResponseEntity.ok(clickByCountry);
    }

    @GetMapping("/click/{shortUrl}")
    public ResponseEntity<Object> getTotalClickByInterval(@PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken, @RequestParam(value = "startTime") String startTime, @RequestParam(value = "endTime") String endTime) {
        Long userId = jwtService.extractId(bearerToken.substring(7));
        Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
        if (urlFound.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
        }
        Url url = urlFound.get();
        User urlUser = url.getUser();
        if (!Objects.equals(urlUser.getId(), userId)) {
            return new ResponseEntity<>(Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
        }
        LocalDateTime startInterval, endInterval;
        try {
            endInterval = LocalDateTime.parse(endTime);
        } catch (DateTimeParseException e) {
            endInterval = LocalDateTime.now();
        }
        try {
            startInterval = LocalDateTime.parse(startTime);
        } catch (DateTimeParseException e) {
            startInterval = endInterval.minusDays(1);
        }
        if (startInterval.isAfter(endInterval)) {
            return new ResponseEntity<>(Map.of("message", "Invalid time interval"), HttpStatus.BAD_REQUEST);
        }
        Map<String, Long> clickLastDay = eventService.getClickByInterval(shortUrl, startInterval, endInterval);
        return ResponseEntity.ok(clickLastDay);
    }

    @GetMapping("/click/{shortUrl}/all")
    public ResponseEntity<Object> getTotalClickByShortUrl(@PathVariable("shortUrl") String shortUrl, @RequestHeader("Authorization") String bearerToken) {
        Long userId = jwtService.extractId(bearerToken.substring(7));
        Optional<Url> urlFound = urlRepository.findFirstByShortUrl(shortUrl);
        if (urlFound.isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND);
        }
        Url url = urlFound.get();
        User urlUser = url.getUser();
        if (!Objects.equals(urlUser.getId(), userId)) {
            return new ResponseEntity<>(Map.of("message", "Unauthorised Access. Url Owner mismatch."), HttpStatus.BAD_REQUEST);
        }
        Map<String, Long> totalClicks = eventService.getTotalClick(shortUrl);
        return ResponseEntity.ok(totalClicks);
    }

}
