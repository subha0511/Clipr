package com.subha.uri.repository;

import com.subha.uri.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    public Optional<Token> findByRefreshToken(String refreshToken);

    public void deleteAllTokensByUserId(Long userId);
}
