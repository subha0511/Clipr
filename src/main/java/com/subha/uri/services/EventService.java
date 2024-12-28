package com.subha.uri.services;

import com.subha.uri.domain.entity.Event;
import com.subha.uri.repository.clickhouse.EventRepository;
import com.subha.uri.repository.postgres.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EventService {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private UrlRepository urlRepository;

  @Async
  public void addEventAsync(Event event) {
    eventRepository.save(event);
  }

  public Map<String, Long> getClickByCountry(
      String shortUrl, LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsByCountry(shortUrl, startTime, endTime);
  }

  public Map<String, Long> getEventByIpAddress(
      String shortUrl, LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsByIpAddress(shortUrl, startTime, endTime);
  }

  public Map<String, Long> getEventByReferrer(
      String shortUrl, LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsByReferrer(shortUrl, startTime, endTime);
  }

  public Map<String, Long> getEventByUserAgent(
      String shortUrl, LocalDateTime startTime, LocalDateTime endTime) {
    return eventRepository.getEventsByUserAgent(shortUrl, startTime, endTime);
  }

  public Map<String, Long> getClickByInterval(
      String shortUrl, LocalDateTime startTime, LocalDateTime endTime) {
    Map<String, Long> results;
    if (startTime.isBefore(endTime.minusDays(1))) {
      results = eventRepository.getEventsByDateRange(shortUrl, startTime, endTime);
    } else {
      results = eventRepository.getEventsByHourRange(shortUrl, startTime, endTime);
    }
    return results;
  }

  public Map<String, Long> getTotalClick(String shortUrl) {
    return Map.of("count", eventRepository.getTotalEvents(shortUrl));
  }
}
