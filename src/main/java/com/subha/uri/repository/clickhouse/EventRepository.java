package com.subha.uri.repository.clickhouse;

import com.subha.uri.domain.entity.Event;
import com.subha.uri.utils.DateTimeUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EventRepository {

  @Autowired
  @Qualifier("clickHouseJdbcTemplate")
  private JdbcTemplate clickHouseJdbcTemplate;

  @Async
  @Transactional
  public void save(Event event) {
    event.setId(UUID.randomUUID());

    String sql = """
        INSERT INTO events
        (id, short_url, created_at, user_agent, referred, country, ip_address)
        SETTINGS async_insert=1, wait_for_async_insert=1
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
    clickHouseJdbcTemplate.update(sql, event.getId(), event.getShortUrl(),
        Timestamp.valueOf(event.getCreatedAt().toLocalDateTime()), event.getUserAgent(),
        event.getReferred(), event.getCountry(), event.getIpAddress());
  }

  public Map<String, Long> getEventsByCountry(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT country, count(*) as count
        FROM events
        WHERE short_url = ? AND (created_at >= toDateTime(?,'UTC')) AND (created_at <= toDateTime(?,'UTC'))
        GROUP BY country
        """;
    return clickHouseJdbcTemplate.query(sql, PreparedStatementSetter -> {
          PreparedStatementSetter.setString(1, shortUrl);
          PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
          PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
        }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(
            rs.getString("country") == null ? "Unknown" : rs.getString("country"), rs.getLong("count")))
        .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Map<String, Long> getEventsByUserAgent(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT user_agent, count(*) as count
        FROM events
        WHERE short_url = ? AND (created_at >= toDateTime(?,'UTC')) AND (created_at <= toDateTime(?,'UTC'))
        GROUP BY user_agent
        """;
    return clickHouseJdbcTemplate.query(sql, PreparedStatementSetter -> {
          PreparedStatementSetter.setString(1, shortUrl);
          PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
          PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
        }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(
            rs.getString("user_agent") == null ? "Unknown" : rs.getString("user_agent"),
            rs.getLong("count"))).stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Map<String, Long> getEventsByIpAddress(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT ip_address, count(*) as count
        FROM events
        WHERE short_url = ? AND (created_at >= toDateTime(?,'UTC')) AND (created_at <= toDateTime(?,'UTC'))
        GROUP BY ip_address
        """;
    return clickHouseJdbcTemplate.query(sql, PreparedStatementSetter -> {
          PreparedStatementSetter.setString(1, shortUrl);
          PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
          PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
        }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(
            rs.getString("ip_address") == null ? "Unknown" : rs.getString("ip_address"),
            rs.getLong("count"))).stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Map<String, Long> getEventsByReferrer(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT referred, count(*) as count
        FROM events
        WHERE short_url = ? AND (created_at >= toDateTime(?,'UTC')) AND (created_at <= toDateTime(?,'UTC'))
        GROUP BY referred
        """;
    return clickHouseJdbcTemplate.query(sql, PreparedStatementSetter -> {
          PreparedStatementSetter.setString(1, shortUrl);
          PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
          PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
        }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(
            rs.getString("referred") == null ? "Unknown" : rs.getString("referred"),
            rs.getLong("count"))).stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Map<String, Long> getEventsByDateRange(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT toDate(created_at) AS date, count(*) as count FROM events
        WHERE short_url = ? AND (created_at >= toDateTime(?,'UTC')) AND (created_at <= toDateTime(?,'UTC'))
        GROUP BY date
        ORDER BY date DESC
        """;
    return clickHouseJdbcTemplate.query(sql, PreparedStatementSetter -> {
          PreparedStatementSetter.setString(1, shortUrl);
          PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
          PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
        }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(rs.getString("date"), rs.getLong("count")))
        .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Map<String, Long> getEventsByHourRange(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT formatDateTime(created_at, '%e-%c-%y %k:00') as time, count(*) as count FROM events
        WHERE short_url = ? AND (created_at >= toDateTime(?,'UTC')) AND (created_at <= toDateTime(?,'UTC'))
        GROUP BY time
        """;
    return clickHouseJdbcTemplate.query(sql, PreparedStatementSetter -> {
          PreparedStatementSetter.setString(1, shortUrl);
          PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
          PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
        }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(rs.getString("time"), rs.getLong("count")))
        .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Long getTotalEvents(String shortUrl) {
    String sql = "SELECT count(*) FROM events WHERE short_url = ?";
    return clickHouseJdbcTemplate.queryForObject(sql, Long.class, shortUrl);
  }

  public List<Event> getEvents() {
    String sql = "SELECT id, short_url, created_at, user_agent, referred, country, ip_address FROM events";
    return clickHouseJdbcTemplate.query(sql, (rs, rowNum) -> {
      Event event = new Event();
      event.setId(UUID.fromString(rs.getString("id")));
      event.setShortUrl(rs.getString("short_url"));
      event.setCreatedAt(Timestamp.valueOf(rs.getTimestamp("created_at").toLocalDateTime()));
      event.setShortUrl(rs.getString("user_agent"));
      event.setShortUrl(rs.getString("referred"));
      event.setShortUrl(rs.getString("country"));
      event.setShortUrl(rs.getString("ip_address"));
      return event;
    });
  }
}
