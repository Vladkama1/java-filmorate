package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "eventId", target = "id")
    @Mapping(target = "timestamp", ignore = true)
    Event toModel(EventDto eventDto);

    @Mapping(source = "id", target = "eventId")
    @Mapping(target = "timestamp", expression = "java(event.getTimestamp().toEpochMilli())")
    EventDto toDTO(Event event);

    List<Event> toListModels(List<EventDto> eventDtoList);

    List<EventDto> toListDTO(List<Event> eventList);
}
