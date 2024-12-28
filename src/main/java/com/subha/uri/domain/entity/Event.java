package com.subha.uri.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
