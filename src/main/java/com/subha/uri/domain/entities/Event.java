package com.subha.uri.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="events")
public class Event {

    @Id
    @GeneratedValue()
    private Long id;

    @NotBlank
    private String shortUrl;

    @CreationTimestamp
    private Timestamp createdAt;

    private String userAgent;

    private String referred;

    private String country;

    private String ipAddress;
}
