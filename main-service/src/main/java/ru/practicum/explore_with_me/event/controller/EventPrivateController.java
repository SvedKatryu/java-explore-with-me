package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.error.exeption.ConflictException;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventDto;
import ru.practicum.explore_with_me.event.service.EventService;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.dto.RequestRequestDto;
import ru.practicum.explore_with_me.request.dto.RequestResultDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@RequestBody @Valid NewEventDto newEventDto,
                                 @PathVariable @Positive Long userId) {
        log.info("Response from POST request on {}", "/users/{userId}/events");
        if (newEventDto.getEventDate() != null) checkEventStart(newEventDto.getEventDate());
        if (newEventDto.getPaid() == null) newEventDto.setPaid(false);
        if (newEventDto.getParticipantLimit() == null) newEventDto.setParticipantLimit(0L);
        if (newEventDto.getRequestModeration() == null) newEventDto.setRequestModeration(true);
        return eventService.addEvent(newEventDto, userId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable @Positive Long userId,
                                 @PathVariable @Positive Long eventId) {
        log.info("Response from GET request on {}{}", "/users/{userId}/events", "/{eventId}");
        return eventService.getEvent(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable @Positive Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Response from GET request on {}", "/users/{userId}/events");
        return eventService.getAllEvents(userId, fromSizePage(from, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Valid UpdateEventDto updateEventDto,
                                    @PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId) {
        log.info("Response from PATCH request on {}{}", "/users/{userId}/events", "/{eventId}");
        if (updateEventDto.getEventDate() != null) checkEventStart(updateEventDto.getEventDate());
        return eventService.updateEvent(updateEventDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId) {
        log.info("Response from GET request on {}{}", "/users/{userId}/events", "/{eventId}/requests");
        return eventService.getRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestResultDto updateRequestsStatus(@RequestBody @Valid RequestRequestDto requestRequestDto,
                                                 @PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId) {
        log.info("Response from PATCH request on {}{}", "/users/{userId}/events", "/{eventId}/requests");
        return eventService.updateRequestsStatus(requestRequestDto, userId, eventId);
    }

    public static PageRequest fromSizePage(int from, int size) {
        return PageRequest.of(from / size, size);
    }

    public static void checkEventStart(LocalDateTime start) {
        if (start != null && start.isBefore(LocalDateTime.now().plusHours(2)))
            throw new ConflictException("The event can only start in two hours");
    }
}
