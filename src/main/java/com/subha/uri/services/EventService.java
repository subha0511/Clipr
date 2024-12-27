package com.subha.uri.services;

import com.subha.uri.domain.entity.CEvent;
import com.subha.uri.domain.entity.Event;
import com.subha.uri.repository.clickhouse.CEventRepository;
import com.subha.uri.repository.postgres.EventRepository;
import com.subha.uri.repository.postgres.UrlRepository;
import com.subha.uri.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EventService {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private CEventRepository cEventRepository;

  @Autowired
  private UrlRepository urlRepository;

  @Async
  public void addEventAsync(Event event) {
    CompletableFuture.completedFuture(eventRepository.save(event));
  }

  @Async
  public void addCEventAsync(CEvent event) {
    cEventRepository.save(event);
  }

  public Map<String, Long> getClickByCountry(String shortUrl) {
    return cEventRepository.getClicksByCountry(shortUrl,
        DateTimeUtils.convertToUtc(LocalDateTime.now().minusYears(20)),
        DateTimeUtils.convertToUtc(LocalDateTime.now().plusDays(1)));
  }

  public Map<String, Long> getClickByInterval(
      String shortUrl, LocalDateTime startTime, LocalDateTime endTime) {
    Map<String, Long> results;
    if (startTime.isBefore(endTime.minusDays(1))) {
      results = cEventRepository.getEventsByDateRange(shortUrl, startTime, endTime);
    } else {
      results = cEventRepository.getEventsByHourRange(shortUrl, startTime, endTime);
    }
    return results;
  }

  public Map<String, Long> getTotalClick(String shortUrl) {
    return Map.of("count", cEventRepository.getTotalClicks(shortUrl));
  }
}
