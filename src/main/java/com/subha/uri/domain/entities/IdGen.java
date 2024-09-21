package com.subha.uri.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "id-gen")
public class IdGen {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Long id;

}
