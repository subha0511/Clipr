package com.subha.uri.domain.entity;

import jakarta.persistence.*;
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
