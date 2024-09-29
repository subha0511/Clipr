package com.subha.uri.controllers;

import com.subha.uri.domain.entities.Url;
import com.subha.uri.exception.ResourceNotFoundException;
import com.subha.uri.services.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/r")
public class UrlRedirectController {

    @Autowired
    private UrlService urlService;

    @GetMapping(path = "/hello")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping("/{shortURL}")
    public RedirectView redirectUrl(@PathVariable("shortURL") String shortURL, HttpServletResponse response) throws IOException {
        Optional<Url> urlFound = urlService.getByShortURL(shortURL);
        if (urlFound.isEmpty()) {
            throw new ResourceNotFoundException("URL not found for hash: " + shortURL);
        }
        String longURL = urlFound.get()
                .getLongURL();
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longURL);
        redirectView.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
        return redirectView;
    }

}

