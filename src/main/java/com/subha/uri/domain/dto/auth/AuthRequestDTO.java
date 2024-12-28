package com.subha.uri.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequestDTO {

  @NotBlank(message = "Email is required")
  @Email(message = "The email address is invalid", flags = {Pattern.Flag.CASE_INSENSITIVE})
  private String email;

  @NotBlank(message = "Password is required")
  @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,32}$", message =
      "Password Rules. Length - 8-32, Min 1 " +
          "lowercase, Min 1 uppercase")
  private String password;

}
