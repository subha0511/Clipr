package com.subha.uri.controllers;

import com.subha.uri.domain.entities.Url;
import com.subha.uri.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
public class UrlRedirectController {

    @Autowired
    private UrlService urlService;

    @GetMapping(path = "/hello")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping(path = "/{hash}")
    public ResponseEntity<?> redirectUrl(@PathVariable("hash") String hash) {
        Optional<Url> urlFound = urlService.getByHash(hash);
        return urlFound.map(url -> ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                        .location(URI.create(url
                                .getUrl()))
                        .build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .build());
    }
}
