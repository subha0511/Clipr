package com.subha.uri.controllers;

import com.subha.uri.domain.dto.UrlDTO;
import com.subha.uri.domain.entity.Url;
import com.subha.uri.mappers.Mapper;
import com.subha.uri.services.JwtService;
import com.subha.uri.services.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Url Shortener", description = "Url Shortener API")
@RestController
@RequestMapping("/urls")
public class UrlShortenerController {

  @Autowired
  private UrlService urlService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private Mapper<Url, UrlDTO> urlMapper;

  @Operation(summary = "Get all urls", description = "Get all urls created by the user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Get list of url objects", content = {
          @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UrlDTO.class)))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not found", content = @Content)})
  @GetMapping(path = "/")
  public List<UrlDTO> getAllUrl(
      @RequestHeader("Authorization") String bearerToken,
      @RequestParam(defaultValue = "0") final Integer pageNumber,
      @RequestParam(defaultValue = "5") final Integer size) {
    Long userId = jwtService.extractId(bearerToken.substring(7));

    Pageable pageable = PageRequest.of(pageNumber, size);
    Page<Url> urls = urlService.findAllUrlsByUserId(userId, pageable);
    return urls.stream().map(urlMapper::mapTo).toList();
  }

  @Operation(summary = "Create a new shortened url", description = "Create a new url")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Url created successfully", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = UrlDTO.class))}),
      @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
  @PostMapping(path = "/shorten")
  public ResponseEntity<UrlDTO> createUrl(
      @RequestHeader("Authorization") String bearerToken, @Valid @RequestBody UrlDTO urlDto) {
    Long userId = jwtService.extractId(bearerToken.substring(7));
    Url url = urlMapper.mapFrom(urlDto);
    Url newUrl = urlService.save(url, userId);
    return new ResponseEntity<>(urlMapper.mapTo(newUrl), HttpStatus.CREATED);
  };

  @Operation(summary = "Get a url", description = "Get a url by id")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "Url found", content = {
//          @Content(mediaType = "application/json", schema = @Schema(implementation = UrlDTO.class)})},
//      @ApiResponse(responseCode = "404", description = "Url not found", content = @Content)})
  @GetMapping(path = "/{id}")
  public ResponseEntity<?> getUrl(@PathVariable("id") Long id) {
    Optional<Url> urlFound = urlService.getById(id);
    return urlFound.map(url -> {
      Object urlDto = urlMapper.mapTo(url);
      return new ResponseEntity<>(urlDto, HttpStatus.OK);
    }).orElse(new ResponseEntity<>(Map.of("message", "Url not found"), HttpStatus.NOT_FOUND));
  }

  @Operation(summary = "Delete a url", description = "Delete a url by id")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Url deleted successfully", content = @Content),
      @ApiResponse(responseCode = "404", description = "Url not found", content = @Content)})
  @DeleteMapping(path = "/{id}")
  public ResponseEntity<?> deleteUrl(@PathVariable("id") Long id) {
    urlService.deleteById(id);
    return new ResponseEntity<>(Map.of("message", "Url deleted successfully"), HttpStatus.OK);
  }
}
