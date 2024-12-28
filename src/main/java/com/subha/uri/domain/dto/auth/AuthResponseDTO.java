package com.subha.uri.domain.dto.auth;

import com.subha.uri.domain.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

  private UserDTO user;

  private String token;

}
