package com.subha.uri.repository;

import com.subha.uri.domain.entities.IdGen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdRepository extends JpaRepository<IdGen, Long> {
}
