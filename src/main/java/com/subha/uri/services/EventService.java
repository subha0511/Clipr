package com.subha.uri.services;

import com.subha.uri.domain.entities.Event;
import com.subha.uri.repository.EventRepository;
import com.subha.uri.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UrlRepository urlRepository;

    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    public Map<String, Long> getClickByCountry(String shortUrl) {
        List<Object[]> results = eventRepository.getTotalClicksByCountry(shortUrl);
        System.out.println(results);
        Map<String, Long> analytics = new HashMap<>();
        for (Object[] result : results) {
            String country = result[0] != null ? (String) result[0] : "Unknown";
            Long count = (Long) result[1];
            analytics.put(country, count);
        }
        return analytics;
    }

    public Map<String, Long> getClickByInterval(String shortUrl, LocalDateTime startInterval, LocalDateTime endInterval) {
        List<Object[]> results = eventRepository.findEventCountPerInterval(shortUrl, startInterval, endInterval);
//        List<Object[]> results = eventRepository.findEventCountPerHourLast24Hours(shortUrl);

        Map<String, Long> eventsPerHour = new HashMap<>();
        for (Object[] row : results) {
            String hour = row[0].toString();  // hour bucket
            Long count = ((Number) row[1]).longValue();  // event count
            eventsPerHour.put(hour, count);
        }
        return eventsPerHour;
    }

    public Map<String, Long> getTotalClick(String shortUrl) {
        return Map.of("count", eventRepository.countByShortUrl(shortUrl));
    }
}
