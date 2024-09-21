package com.subha.uri.services;

import com.subha.uri.domain.entities.IdGen;
import com.subha.uri.repository.IdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdService {

    @Autowired
    private IdRepository idRepository;

    public Long getUniqueId() {
        return idRepository.save(new IdGen())
                .getId();
    }
}
