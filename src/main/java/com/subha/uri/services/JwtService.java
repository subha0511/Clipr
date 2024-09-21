package com.subha.uri.services;

import com.subha.uri.config.jwt.JwtKeyLocator;
import com.subha.uri.config.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret.access-key}")
    private String secret;

    @Value("${jwt.secret.refresh-key}")
    private String refreshSecret;

    @Value("${jwt.secret.expiration}")
    private Long expiration;

    @Value("${jwt.secret.refresh-expiration}")
    private Long refreshExpiration;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtKeyLocator jwtKeyLocator;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .keyLocator(jwtKeyLocator)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        SecretKey secretKey = jwtProperties.getSecretKey("access");
        return Jwts.builder()
                .header()
                .keyId("access")
                .and()
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpiration()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        SecretKey secretKey = jwtProperties.getSecretKey("refresh");
        return Jwts.builder()
                .header()
                .keyId("refresh")
                .and()
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpiration()))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetail) {
        final String username = extractUsername(token);
        return (username.equals(userDetail.getUsername())) && isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractId(String token) {
        Map<String, Object> claims = extractAllClaims(token);
        return Long.parseLong(claims.get("id")
                .toString());
    }

}
