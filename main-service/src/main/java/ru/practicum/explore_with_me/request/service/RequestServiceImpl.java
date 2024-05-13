package ru.practicum.explore_with_me.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.enums.EventState;
import ru.practicum.explore_with_me.enums.RequestStatus;
import ru.practicum.explore_with_me.error.exeption.ConflictException;
import ru.practicum.explore_with_me.error.exeption.NotFoundException;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.request.Request;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.repository.RequestRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explore_with_me.enums.RequestStatus.CANCELED;
import static ru.practicum.explore_with_me.enums.RequestStatus.CONFIRMED;
import static ru.practicum.explore_with_me.enums.RequestStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        log.debug("Adding request from user ID{} to event ID{}", userId, eventId);
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException(String.format("User ID%d already send request on event ID%d", userId, eventId));
        }
        User user = getUserIfPresent(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event ID%d not found", eventId)));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("The event manager is unable to make a request for his event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(String.format("Event ID%d still not published", eventId));
        }
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Limit of the participants is already reached");
        }
        RequestStatus status;
        status = event.getRequestModeration() ? PENDING : CONFIRMED;
        if (event.getParticipantLimit() == 0) status = CONFIRMED;
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(status)
                .build();
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequests(Long userId) {
        log.debug(String.format("Getting requests of user ID%d", userId));
        getUserIfPresent(userId);
        return requestMapper.toRequestDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.debug(String.format("Canceling request ID%d", requestId));
        User user = getUserIfPresent(userId);
        Request request = requestRepository.findRequestByRequesterAndId(user, requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request ID%d doesn't exist", requestId)));
        request.setStatus(CANCELED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    private User getUserIfPresent(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User ID%d not found", userId)));
    }
}
