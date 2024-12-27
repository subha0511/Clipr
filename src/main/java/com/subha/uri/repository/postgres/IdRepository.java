package com.subha.uri.repository.postgres;

import com.subha.uri.domain.entity.IdGen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdRepository extends JpaRepository<IdGen, Long> {
}
