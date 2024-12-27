package com.subha.uri.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "urls")
public class Url implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "long_url", nullable = false)
    private String longUrl;

    @Column(name = "short_url", nullable = false)
    private String shortUrl;

    @JsonBackReference // Back reference for JSON serialization
    @ToString.Exclude  // Prevent Lombok recursion
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;
}
