package com.subha.uri.domain.entity;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    private UUID id;

    private String shortUrl;

    private Timestamp createdAt;

    private String userAgent;

    private String referred;

    private String country;

    private String ipAddress;
}
