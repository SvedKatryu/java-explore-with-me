package ru.practicum.explore_with_me.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable @Positive Long userId,
                                 @RequestParam @Positive Long eventId) {
        log.info("Response from POST request on {}", "/users/{userId}/requests");
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getAllRequests(@PathVariable @Positive Long userId) {
        log.info("Response from GET request on {}", "/users/{userId}/requests");
        return requestService.getAllRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long requestId) {
        log.info("Response from GET request on {}", "/users/{userId}/requests");
        return requestService.cancelRequest(userId, requestId);
    }
}
