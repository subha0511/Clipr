package com.subha.uri.controllers;

import com.subha.uri.domain.entities.Event;
import com.subha.uri.domain.entities.Url;
import com.subha.uri.exception.ResourceNotFoundException;
import com.subha.uri.services.EventService;
import com.subha.uri.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/r")
public class UrlRedirectController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private EventService eventService;

    @GetMapping(path = "/hello")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping("/{shortURL}")
    public RedirectView redirectUrl(@PathVariable("shortURL") String shortUrl, HttpServletRequest request) throws IOException {
        Optional<Url> urlFound = urlService.getByShortURL(shortUrl);
        if (urlFound.isEmpty()) {
            throw new ResourceNotFoundException("URL not found for hash: " + shortUrl);
        }
        Url url = urlFound.get();

        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress != null && !ipAddress.isEmpty()) {
            // Take the first IP if multiple IPs are present
            ipAddress = ipAddress.split(",")[0].trim();
        } else {
            ipAddress = request.getHeader("X-Real-IP");
        }

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        String country = request.getHeader("CF-IPCountry");

        Event event = Event.builder()
                .shortUrl(shortUrl)
                .userAgent(userAgent)
                .referred(referer)
                .country(country)
                .ipAddress(ipAddress)
                .build();

        eventService.addEvent(event);

        String longUrl = url.getLongUrl();
        // If URL is missing the protocol, prepend "https://"
        if (!longUrl.startsWith("http://") && !longUrl.startsWith("https://")) {
            longUrl = "https://" + longUrl;
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longUrl);
        return redirectView;
    }

}

