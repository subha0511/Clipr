package com.subha.uri.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
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
