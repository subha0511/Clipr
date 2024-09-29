package com.subha.uri.repository;

import com.subha.uri.domain.entities.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends PagingAndSortingRepository<Url, Long>, JpaRepository<Url,Long> {

    Optional<Url> findFirstByShortURL(String shortUrl);

    Page<Url> findAllUrlsByUserId(Long userId, Pageable pageable);
}
