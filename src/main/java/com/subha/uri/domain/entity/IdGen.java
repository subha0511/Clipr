package com.subha.uri.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "id-gen")
public class IdGen {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  public Long id;

}
