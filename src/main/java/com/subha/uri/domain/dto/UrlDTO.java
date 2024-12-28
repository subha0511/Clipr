package com.subha.uri.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlDTO {

  private Long id;

  @NotBlank(message = "Url cannot be empty")
  private String longUrl;

  private String shortUrl;
}
