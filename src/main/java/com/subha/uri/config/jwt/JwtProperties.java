package com.subha.uri.config.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt.secret")
public class JwtProperties {

  @Value("${jwt.secret.access-key}")
  private String access;

  @Value("${jwt.secret.refresh-key}")
  private String refresh;

  @Value("${jwt.secret.expiration}")
  private Long accessExpiration;

  @Value("${jwt.secret.refresh-expiration}")
  private Long refreshExpiration;

  private Map<String, SecretKey> keysMap;
  private Map<String, Long> expirationMap;

  // Helper method to create a SecretKey from a base64-encoded string
  private SecretKey createSecretKey(String key) {
    byte[] keyBytes = Decoders.BASE64.decode(key);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @PostConstruct
  public void init() {
    keysMap = new HashMap<>();
    expirationMap = new HashMap<>(); // Initialize expirationMap

    // Adding keys to keysMap
    keysMap.put("access", createSecretKey(access));
    keysMap.put("refresh", createSecretKey(refresh));

    // Adding expiration values to expirationMap
    expirationMap.put("access", accessExpiration);
    expirationMap.put("refresh", refreshExpiration);
  }

  // Retrieve the SecretKey for a given key (defaults to access if not found)
  public SecretKey getSecretKey(String key) {
    return keysMap.getOrDefault(key, keysMap.get("access"));
  }

  // Retrieve the expiration time for a given key (defaults to access expiration if not found)
  public Long getExpiration(String key) {
    return expirationMap.getOrDefault(key, expirationMap.get("access"));
  }
}
