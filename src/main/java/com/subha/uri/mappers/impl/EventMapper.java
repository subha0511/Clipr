package com.subha.uri.mappers.impl;

import com.subha.uri.domain.dto.EventDTO;
import com.subha.uri.domain.entity.Event;
import com.subha.uri.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventMapper implements Mapper<Event, EventDTO> {

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public EventDTO mapTo(Event event) {
    return modelMapper.map(event, EventDTO.class);
  }

  @Override
  public Event mapFrom(EventDTO eventDto) {
    return modelMapper.map(eventDto, Event.class);
  }
}
