package ru.practicum.explore_with_me.event.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.model.Event;

import java.util.List;
import java.util.Set;

@DecoratedWith(EventMapperDecorator.class)
@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category", target = "category")
    Event toEvent(NewEventDto newEventDto, Category category);

    @Named(value = "shortDto")
    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> toEventShortDto(List<Event> events);

    Set<EventShortDto> toEventShortDto(Set<Event> events);

    EventFullDto toEventFullDto(Event event);
}
