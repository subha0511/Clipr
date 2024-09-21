package com.subha.uri.controllers;

import com.subha.uri.domain.dto.AuthenticationDto;
import com.subha.uri.domain.entities.User;
import com.subha.uri.domain.entities.Token;
import com.subha.uri.mappers.impl.UserMapper;
import com.subha.uri.repository.TokenRepository;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
    private TokenRepository tokenRepository;

    @Value("${cookie.token.expiration}")
    private int tokenExpiration;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, HttpServletResponse response) {
        if (userService.userExists(user)) {
            return new ResponseEntity<>("User Already Exists", HttpStatus.CONFLICT);
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
                .refreshToken(refreshToken)
                .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration))
                .build()
        );

        response.addCookie(cookie);

        return ResponseEntity.ok(AuthenticationDto.builder()
                .user(userMapper.mapTo(savedUser))
                .token(token)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        Optional<User> foundUser = userService.getUserByEmail(user.getEmail());
        return foundUser.map(savedUser -> {

                    Map<String, Object> payload = new HashMap<>();
                    payload.put("id", savedUser.getId());

                    String refreshToken = jwtService.generateRefreshToken(payload, user);
                    String token = jwtService.generateToken(payload, user);

                    Cookie cookie = new Cookie("refreshToken", refreshToken);
                    cookie.setMaxAge(tokenExpiration);
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);

                    response.addCookie(cookie);

                    tokenRepository.save(Token
                            .builder()
                            .user(savedUser)
                            .refreshToken(refreshToken)
                            .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration))
                            .build()
                    );

                    return ResponseEntity.ok(AuthenticationDto.builder()
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
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
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
                                .refreshToken(newRefreshToken)
                                .expiration(new Timestamp(System.currentTimeMillis() + tokenExpiration))
                                .build()
                        );

                        Cookie cookie = new Cookie("refreshToken", refreshToken);
                        cookie.setMaxAge(tokenExpiration);
                        cookie.setSecure(true);
                        cookie.setHttpOnly(true);

                        response.addCookie(cookie);

                        return ResponseEntity.ok(AuthenticationDto.builder()
                                .token(newAccesstoken)
                                .build());
                    }
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                })
                .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @Transactional
    @GetMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String bearerToken) {
        Long userId = jwtService.extractId(bearerToken.substring(7));
        tokenRepository.deleteAllTokensByUserId(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
