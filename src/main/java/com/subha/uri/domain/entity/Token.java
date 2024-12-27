package com.subha.uri.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"refreshToken"}),
})
public class Token {

    @Id
    private String id;

    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Timestamp expiration;

}
