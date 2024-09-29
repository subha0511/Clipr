package com.subha.uri.controllers;

import com.subha.uri.domain.dto.auth.AuthRequestDTO;
import com.subha.uri.domain.dto.auth.AuthResponseDTO;
import com.subha.uri.domain.entities.Token;
import com.subha.uri.domain.entities.User;
import com.subha.uri.mappers.impl.AuthMapper;
import com.subha.uri.mappers.impl.UserMapper;
import com.subha.uri.repository.TokenRepository;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequestDTO authRequestDTO,
                                      HttpServletResponse response) {
        User user = authMapper.mapFrom(authRequestDTO);
        if (userService.userExists(user)) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "User Already Exists");
            return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", user.getId());
        String refreshToken = jwtService.generateRefreshToken(payload, user);
        String token = jwtService.generateToken(payload, user);

        User savedUser = userService.saveUser(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(tokenExpiration);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        tokenRepository.save(Token
                .builder()
                .user(savedUser)
                .refreshToken(passwordEncoder.encode(refreshToken))
                .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration))
                .build()
        );

        response.addCookie(cookie);

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .user(userMapper.mapTo(savedUser))
                .token(token)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO,
                                                 HttpServletResponse response) {
        Optional<User> foundUser = userService.getUserByEmail(authRequestDTO.getEmail());
        return foundUser.map(savedUser -> {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("id", savedUser.getId());
                    String refreshToken = jwtService.generateRefreshToken(payload, savedUser);
                    String token = jwtService.generateToken(payload, savedUser);

                    Cookie cookie = new Cookie("refreshToken", refreshToken);
                    cookie.setMaxAge(tokenExpiration);
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);

                    response.addCookie(cookie);

                    tokenRepository.save(Token
                            .builder()
                            .user(savedUser)
                            .refreshToken(passwordEncoder.encode(refreshToken))
                            .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration))
                            .build()
                    );

                    return ResponseEntity.ok(AuthResponseDTO.builder()
                            .user(userMapper.mapTo(savedUser))
                            .token(token)
                            .build());
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Token> token = tokenRepository.findByRefreshToken(passwordEncoder.encode(refreshToken));
        return token.map(foundToken -> {
                    User user = foundToken.getUser();
                    if (jwtService.isTokenValid(refreshToken, user)) {
                        tokenRepository.delete(foundToken);

                        Map<String, Object> payload = new HashMap<>();
                        payload.put("id", user.getId());

                        String newAccesstoken = jwtService.generateToken(payload, user);
                        String newRefreshToken = jwtService.generateRefreshToken(payload, user);

                        tokenRepository.save(Token
                                .builder()
                                .user(user)
                                .refreshToken(passwordEncoder.encode(newRefreshToken))
                                .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration))
                                .build()
                        );

                        Cookie cookie = new Cookie("refreshToken", newRefreshToken);
                        cookie.setMaxAge(tokenExpiration);
                        cookie.setSecure(true);
                        cookie.setHttpOnly(true);

                        response.addCookie(cookie);

                        return ResponseEntity.ok(AuthResponseDTO.builder()
                                .token(newAccesstoken)
                                .build());
                    }
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                })
                .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @Transactional
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        Long userId = jwtService.extractId(bearerToken.substring(7));
        tokenRepository.deleteAllTokensByUserId(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
