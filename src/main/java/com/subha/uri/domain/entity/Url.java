package com.subha.uri.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
