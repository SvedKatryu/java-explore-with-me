package ru.practicum.explore_with_me.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.enums.EventState;
import ru.practicum.explore_with_me.error.exeption.ConflictException;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventDto;
import ru.practicum.explore_with_me.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@RequestParam(name = "users", defaultValue = "") List<Long> usersIds,
                                               @RequestParam(defaultValue = "") List<EventState> states,
                                               @RequestParam(name = "categories", defaultValue = "") List<Long> categoriesIds,
                                               @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Response from GET request on {}", "/admin/events");
        return eventService.getEventsByAdmin(usersIds, states, categoriesIds, rangeStart, rangeEnd,
                fromSizePage(from, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@RequestBody @Valid UpdateEventDto updateEventDto,
                                           @PathVariable Long eventId) {
        log.info("Response from PATCH request on {}", "/admin/events" + "/{eventId}");
        if (updateEventDto.getEventDate() != null) checkEventStart(updateEventDto.getEventDate());
        return eventService.updateEventByAdmin(updateEventDto, eventId);
    }

    public static PageRequest fromSizePage(int from, int size) {
        return PageRequest.of(from / size, size);
    }

    public static void checkEventStart(LocalDateTime start) {
        if (start != null && start.isBefore(LocalDateTime.now().plusHours(2)))
            throw new ConflictException("The event can only start in two hours");
    }
}
