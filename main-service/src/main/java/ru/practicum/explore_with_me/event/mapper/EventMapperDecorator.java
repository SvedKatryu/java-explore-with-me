package ru.practicum.explore_with_me.event.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.model.Event;

public abstract class EventMapperDecorator implements EventMapper {
    @Autowired
    private EventMapper eventMapper;

    @Override
    public Event toEvent(NewEventDto newEventDto, Category category) {
        Event event = eventMapper.toEvent(newEventDto, category);
        event.setCategory(category);
        return event;
    }
}
