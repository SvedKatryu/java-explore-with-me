package ru.practicum.explore_with_me.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore_with_me.enums.EventSort;
import ru.practicum.explore_with_me.enums.EventState;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventDto;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.dto.RequestRequestDto;
import ru.practicum.explore_with_me.request.dto.RequestResultDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
   EventFullDto addEvent(NewEventDto newEventDto, Long userId);

    EventFullDto  getEvent(Long userId, Long eventId);

    List<EventShortDto> getAllEvents(Long userId, PageRequest pageRequest);

    EventFullDto updateEvent(UpdateEventDto updateEventDto, Long userId, Long eventId);

    List<RequestDto> getRequestsByEventId(Long userId, Long eventId);

    RequestResultDto updateRequestsStatus(RequestRequestDto requestUpdateDto, Long userId, Long eventId);

    List<EventFullDto> getEventsByAdmin(List<Long> usersIds, List<EventState> states, List<Long> categoriesIds,
                                        LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    EventFullDto updateEventByAdmin(UpdateEventDto userUpdateEventDto, Long eventId);

    List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                           PageRequest pageRequest, HttpServletRequest request);

    EventFullDto getPublishedEventById(Long id, HttpServletRequest request);

    public List<Event> fillWithEventViews(List<Event> events);

    List<Event> fillWithConfirmedRequests(List<Event> events);
}
