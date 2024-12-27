package com.subha.uri.repository.clickhouse;

import com.subha.uri.domain.entity.CEvent;
import com.subha.uri.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CEventRepository {

  @Autowired
  @Qualifier("clickHouseJdbcTemplate")
  private JdbcTemplate clickHouseJdbcTemplate;

  @Async
  public void save(CEvent event) {
    event.setId(UUID.randomUUID());

    String sql = """
        INSERT INTO events
        (id, short_url, created_at, user_agent, referred, country, ip_address)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
    clickHouseJdbcTemplate.update(sql, event.getId(), event.getShortUrl(),
        Timestamp.valueOf(event.getCreatedAt().toLocalDateTime()), event.getUserAgent(),
        event.getReferred(), event.getCountry(), event.getIpAddress());
  }

//    public List<CEvent> getEventsByShortUrl(String shortUrl) {
//        String sql = """
//                SELECT * FROM events
//                WHERE short_url = ?
//                ORDER BY created_at DESC
//                """;
//
//        return clickHouseJdbcTemplate.query(sql, (rs, rowNum) -> new CEvent(UUID.fromString(rs.getString("id")), rs.getString("short_url"), Timestamp.valueOf(rs.getTimestamp("created_at")
//                .toLocalDateTime()), rs.getString("user_agent"), rs.getString("referred"), rs.getString("country"), rs.getString("ip_address")), shortUrl);
//    }

  public Map<String, Long> getClicksByCountry(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT country, count(*) as count
        FROM events
        WHERE short_url = ? AND created_at BETWEEN ? AND ?
        GROUP BY country
        """;
    return clickHouseJdbcTemplate.query(
            sql, PreparedStatementSetter -> {
              PreparedStatementSetter.setString(1, shortUrl);
              PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
              PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
            }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(
                rs.getString("country") == null ? "Unknown" : rs.getString("country"),
                rs.getLong("count"))).stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Map<String, Long> getEventsByDateRange(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    String sql = """
        SELECT toDate(created_at) AS date, count(*) as count FROM events
        WHERE short_url = ?
        AND created_at BETWEEN ? AND ?
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
        SELECT formatDateTime(created_at, '%e-%b-%y %k:00') as time, count(*) as count FROM events
        WHERE short_url = ?
        AND created_at BETWEEN ? AND ?
        GROUP BY time
        """;
    return clickHouseJdbcTemplate.query(sql, PreparedStatementSetter -> {
          PreparedStatementSetter.setString(1, shortUrl);
          PreparedStatementSetter.setString(2, DateTimeUtils.formatToClickhouseFormat(start));
          PreparedStatementSetter.setString(3, DateTimeUtils.formatToClickhouseFormat(end));
        }, (rs, rowNum) -> new AbstractMap.SimpleEntry<>(rs.getString("time"), rs.getLong("count")))
        .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
  }

  public Long getTotalClicks(String shortUrl) {
    String sql = "SELECT count(*) FROM events WHERE short_url = ?";
    return clickHouseJdbcTemplate.queryForObject(sql, Long.class, shortUrl);
  }

  public List<CEvent> getEvents() {
    String sql = "SELECT id, short_url, created_at, user_agent, referred, country, ip_address FROM events";
    return clickHouseJdbcTemplate.query(sql, (rs, rowNum) -> {
      CEvent event = new CEvent();
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
