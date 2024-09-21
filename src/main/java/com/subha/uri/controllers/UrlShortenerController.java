package com.subha.uri.controllers;

import com.subha.uri.domain.dto.UrlDto;
import com.subha.uri.domain.entities.Url;
import com.subha.uri.mappers.Mapper;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/urls")
public class UrlShortenerController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Mapper<Url, UrlDto> urlMapper;

    @GetMapping(path = "/")
    public List<UrlDto> getAllUrl(@RequestHeader("Authorization") String bearerToken,
                                  @RequestParam(defaultValue = "0") final Integer pageNumber,
                                  @RequestParam(defaultValue = "5") final Integer size) {
        Long userId = jwtService.extractId(bearerToken.substring(7));

        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Url> urls = urlService.findAllUrlsByUserId(userId, pageable);
        return urls.stream()
                .map(urlMapper::mapTo)
                .toList();
    }

    @PostMapping(path = "")
    public ResponseEntity<UrlDto> createUrl(@RequestHeader("Authorization") String bearerToken,
                                            @RequestBody UrlDto urlDto) {
        Long userId = jwtService.extractId(bearerToken.substring(7));
        Url url = urlMapper.mapFrom(urlDto);
        Url newUrl = urlService.save(url,userId);
        return new ResponseEntity<UrlDto>(urlMapper.mapTo(newUrl), HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUrl(@PathVariable("id") Long id) {
        Optional<Url> urlFound = urlService.getById(id);
        return urlFound.map(url -> {
                    UrlDto urlDto = urlMapper.mapTo(url);
                    return new ResponseEntity<>(urlDto, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
