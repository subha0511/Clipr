package com.subha.uri.controllers;

import com.subha.uri.domain.dto.auth.AuthRequestDTO;
import com.subha.uri.domain.dto.auth.AuthResponseDTO;
import com.subha.uri.domain.entity.Token;
import com.subha.uri.domain.entity.User;
import com.subha.uri.mappers.impl.AuthMapper;
import com.subha.uri.mappers.impl.UserMapper;
import com.subha.uri.repository.postgres.TokenRepository;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Auth", description = "Authentication API")
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserService userService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private AuthMapper authMapper;

  @Autowired
  private TokenRepository tokenRepository;

  @Value("${cookie.token.expiration}")
  private int tokenExpiration;

  @Operation(summary = "User Login / Register", description = "Allows a user to log in and obtain an access token and a refresh token. Creates a user if user doesn't exist.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login successful, tokens generated.", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input data."),
      @ApiResponse(responseCode = "401", description = "Unauthorized access.")})
  @PostMapping("/login")
  public ResponseEntity<Object> login(
      @Valid @RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response) {
    User user = authMapper.mapFrom(authRequestDTO);

    Optional<User> checkUser = userService.getUserByEmail(authRequestDTO.getEmail());

    User savedUser = checkUser.orElseGet(() -> userService.saveUser(user));

    String tokenId = UUID.randomUUID().toString();

    // Generate New Token in Hibernate
    Token newToken = Token.builder().id(tokenId).user(savedUser)
        .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration * 1000L)).build();

    Map<String, Object> payload = new HashMap<>();
    payload.put("id", savedUser.getId());
    payload.put("token_id", newToken.getId());
    String refreshTokenJwt = jwtService.generateRefreshToken(payload, savedUser);

    newToken.setRefreshToken(passwordEncoder.encode(refreshTokenJwt));
    tokenRepository.save(newToken);

    Cookie cookie = new Cookie("refreshToken", refreshTokenJwt);
    cookie.setMaxAge(tokenExpiration);
    cookie.setSecure(true);
    cookie.setHttpOnly(true);

    response.addCookie(cookie);

    String accessTokenJwt = jwtService.generateToken(payload, savedUser);

    return ResponseEntity.ok(
        AuthResponseDTO.builder().user(userMapper.mapTo(savedUser)).token(accessTokenJwt).build());
  }

  @Operation(summary = "Refresh Token", description = "Refreshes the access token using the refresh token stored in the cookie.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Token refreshed successfully.", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized access or invalid refresh token.")})
  @GetMapping("/refresh")
  public ResponseEntity<?> refresh(
      @CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Refresh token is null"));
    }

    String tokenId = (String) jwtService.extractAllClaims(refreshToken).get("token_id");
    Optional<Token> token = tokenRepository.findById(tokenId);

    if (token.isPresent()) {
      Token foundToken = token.get();

      if (!passwordEncoder.matches(refreshToken, foundToken.getRefreshToken())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", "Refresh token mismatch"));
      }

      User user = foundToken.getUser();

      if (!jwtService.isTokenValid(refreshToken, user)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", "Refresh token expired"));
      }

      tokenRepository.delete(foundToken);

      String newTokenId = UUID.randomUUID().toString();

      Token newToken = Token.builder().id(newTokenId).user(user)
          .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration * 1000L)).build();

      Map<String, Object> payload = new HashMap<>();
      payload.put("id", user.getId());
      payload.put("token_id", newToken.getId());

      String newAccesstoken = jwtService.generateToken(payload, user);
      String newRefreshToken = jwtService.generateRefreshToken(payload, user);

      newToken.setRefreshToken(passwordEncoder.encode(newRefreshToken));
      tokenRepository.save(newToken);

      Cookie cookie = new Cookie("refreshToken", newRefreshToken);
      cookie.setMaxAge(tokenExpiration);
      cookie.setSecure(true);
      cookie.setHttpOnly(true);

      response.addCookie(cookie);

      return ResponseEntity.ok(
          AuthResponseDTO.builder().user(userMapper.mapTo(user)).token(newAccesstoken).build());
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", "Refresh token not found"));
  }

  @Operation(summary = "User Logout", description = "Logs out the user by invalidating all active tokens.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "User logged out successfully."),
      @ApiResponse(responseCode = "401", description = "Unauthorized access.")})
  @Transactional
  @GetMapping("/logout")
  public ResponseEntity<Object> logout(@RequestHeader("Authorization") String bearerToken) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    tokenRepository.deleteAllTokensByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "User Logged Out"));
  }
}

