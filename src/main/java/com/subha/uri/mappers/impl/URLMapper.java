package com.subha.uri.mappers.impl;

import com.subha.uri.domain.dto.UrlDto;
import com.subha.uri.domain.entities.Url;
import com.subha.uri.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLMapper implements Mapper<Url, UrlDto> {

    @Autowired
    private ModelMapper modelMapper;

    
    @Override
    public UrlDto mapTo(Url url) {
        return modelMapper.map(url, UrlDto.class);
    }

    @Override
    public Url mapFrom(UrlDto urlDto) {
        return modelMapper.map(urlDto, Url.class);
    }
}
