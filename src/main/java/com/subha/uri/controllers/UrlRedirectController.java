package com.subha.uri.controllers;

import com.subha.uri.domain.entities.Url;
import com.subha.uri.exception.ResourceNotFoundException;
import com.subha.uri.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@RestController
@RequestMapping("/r")
public class UrlRedirectController {

    @Autowired
    private UrlService urlService;

    @GetMapping(path = "/hello")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping(path = "/{hash}")
    public RedirectView redirectUrl(@PathVariable("hash") String hash) {
        Optional<Url> urlFound = urlService.getByHash(hash);
        return urlFound.map(u -> {
                    System.out.println(u);
                    return new RedirectView(u.getUrl());
                })
                .orElseThrow(() -> new ResourceNotFoundException("URL not found for hash: " + hash));
    }


}

