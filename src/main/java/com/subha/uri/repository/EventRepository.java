package com.subha.uri.repository;

import com.subha.uri.domain.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    Long countByShortUrl(String shortUrl);

    @Query("SELECT e.country, COUNT(e) FROM Event e Where e.shortUrl=?1 GROUP BY e.country")
    List<Object[]> getTotalClicksByCountry(String shortUrl);

    @Query(value = """
            WITH time_series AS (
                SELECT generate_series(
                    CASE
                        WHEN CAST(?2 AS TIMESTAMP) >= CAST(?3 AS TIMESTAMP) - INTERVAL '24 hours' THEN DATE_TRUNC('hour', CAST(?2 AS TIMESTAMP))
                        ELSE DATE_TRUNC('day', CAST(?2 AS TIMESTAMP))
                    END,
                    CASE
                        WHEN CAST(?2 AS TIMESTAMP) >= CAST(?3 AS TIMESTAMP) - INTERVAL '24 hours' THEN DATE_TRUNC('hour', CAST(?3 AS TIMESTAMP))
                        ELSE DATE_TRUNC('day', CAST(?3 AS TIMESTAMP))
                    END,
                    CASE
                        WHEN CAST(?2 AS TIMESTAMP) >= CAST(?3 AS TIMESTAMP) - INTERVAL '24 hours' THEN INTERVAL '1 hour'
                        ELSE INTERVAL '1 day'
                    END
                ) AS time_interval
            ),
            event_counts AS (
                SELECT
                    CASE
                        WHEN CAST(?2 AS TIMESTAMP) >= CAST(?3 AS TIMESTAMP) - INTERVAL '24 hours' THEN DATE_TRUNC('hour', CAST(e.created_at AS TIMESTAMP))
                        ELSE DATE_TRUNC('day', CAST(e.created_at AS TIMESTAMP))
                    END AS event_time,
                    COUNT(e.created_at) AS event_count
                FROM public.events e
                WHERE e.short_url = ?1
                  AND e.created_at BETWEEN CAST(?2 AS TIMESTAMP) AND CAST(?3 AS TIMESTAMP)
                GROUP BY event_time
            )
            SELECT
                ts.time_interval AS time_slot,
                COALESCE(ec.event_count, 0) AS event_count
            FROM time_series ts
            LEFT JOIN event_counts ec
                ON ts.time_interval = ec.event_time
            ORDER BY ts.time_interval;
            """, nativeQuery = true)
    List<Object[]> findEventCountPerInterval(@Param("shortUrl") String short_url, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
