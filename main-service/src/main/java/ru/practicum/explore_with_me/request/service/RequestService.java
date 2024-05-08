package ru.practicum.explore_with_me.request.service;

import ru.practicum.explore_with_me.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    List<RequestDto> getAllRequests(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);
}
