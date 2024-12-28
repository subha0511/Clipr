package com.subha.uri.mappers.impl;

import com.subha.uri.domain.dto.UserDTO;
import com.subha.uri.domain.entity.User;
import com.subha.uri.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDTO> {

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public UserDTO mapTo(User user) {
    return modelMapper.map(user, UserDTO.class);
  }

  @Override
  public User mapFrom(UserDTO userDto) {
    return modelMapper.map(userDto, User.class);
  }
}
