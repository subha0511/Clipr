package com.subha.uri.repository.postgres;

import com.subha.uri.domain.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends PagingAndSortingRepository<Url, Long>, JpaRepository<Url, Long> {

    @Query(value = "SELECT u FROM Url u JOIN FETCH u.user WHERE u.shortUrl = ?1")
    Optional<Url> findUrlUserByShortUrl(String id);

    //    @EntityGraph(attributePaths = {"user"})
    Optional<Url> findFirstByShortUrl(String shortUrl);

    Page<Url> findAllUrlsByUserId(Long userId, Pageable pageable);
}
