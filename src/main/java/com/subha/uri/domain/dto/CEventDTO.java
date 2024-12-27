package com.subha.uri.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CEventDTO {

    private String id;

    private String shortURL;

    private Timestamp createdAt;

    private String userAgent;

    private String referred;

    private String country;
}
