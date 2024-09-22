package com.subha.uri.mappers.impl;

import com.subha.uri.domain.dto.auth.AuthRequestDTO;
import com.subha.uri.domain.entities.User;
import com.subha.uri.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper implements Mapper<User, AuthRequestDTO> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AuthRequestDTO mapTo(User user) {
        return modelMapper.map(user, AuthRequestDTO.class);
    }

    @Override
    public User mapFrom(AuthRequestDTO authRequestDTO) {
        return modelMapper.map(authRequestDTO, User.class);
    }
}