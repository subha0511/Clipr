package com.subha.uri.services;

import com.subha.uri.domain.entities.Url;
import com.subha.uri.domain.entities.User;
import com.subha.uri.repository.UrlRepository;
import com.subha.uri.repository.UserRepository;
import com.subha.uri.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdService idService;

    public Url save(Url url,Long userId) {
        Long uniqueId = idService.getUniqueId();
        String hash = IdUtil.encodeId(uniqueId);
        hash = "0000000".substring(hash.length()) + hash;

        User user = userRepository.getReferenceById(userId);
        url.setHash(hash);
        url.setUser(user);

        return urlRepository.save(url);
    }

    public Optional<Url> getById(Long id) {
        return urlRepository.findById(id);
    }

    public Optional<Url> getByHash(String hash) {
        return urlRepository.findFirstByHash(hash);
    }

    public Page<Url> findAllUrlsByUserId(Long userId, Pageable pageable) {
        return urlRepository.findAllUrlsByUserId(userId, pageable);
    }
}
