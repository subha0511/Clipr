package com.subha.uri.mappers.impl;

import com.subha.uri.domain.dto.UrlDTO;
import com.subha.uri.domain.entity.Url;
import com.subha.uri.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLMapper implements Mapper<Url, UrlDTO> {

    @Autowired
    private ModelMapper modelMapper;

    
    @Override
    public UrlDTO mapTo(Url url) {
        return modelMapper.map(url, UrlDTO.class);
    }

    @Override
    public Url mapFrom(UrlDTO urlDto) {
        return modelMapper.map(urlDto, Url.class);
    }
}
