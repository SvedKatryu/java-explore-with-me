package ru.practicum.explore_with_me.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.HitsDto;
import ru.practicum.explore_with_me.StatsClient;
import ru.practicum.explore_with_me.StatsDto;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.enums.EventSort;
import ru.practicum.explore_with_me.enums.EventState;
import ru.practicum.explore_with_me.enums.RequestStatus;
import ru.practicum.explore_with_me.error.exeption.ConflictException;
import ru.practicum.explore_with_me.error.exeption.NotFoundException;
import ru.practicum.explore_with_me.event.dto.EventFullDto;
import ru.practicum.explore_with_me.event.dto.EventShortDto;
import ru.practicum.explore_with_me.event.dto.NewEventDto;
import ru.practicum.explore_with_me.event.dto.UpdateEventDto;
import ru.practicum.explore_with_me.event.mapper.EventMapper;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.location.mapper.LocationMapper;
import ru.practicum.explore_with_me.location.model.Location;
import ru.practicum.explore_with_me.location.repository.LocationRepository;
import ru.practicum.explore_with_me.request.Request;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.request.dto.RequestRequestDto;
import ru.practicum.explore_with_me.request.dto.RequestResultDto;
import ru.practicum.explore_with_me.request.mapper.RequestMapper;
import ru.practicum.explore_with_me.request.repository.RequestRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RequestMapper requestMapper;

    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto newEventDto, Long userId) {
        log.debug("Adding event {}", newEventDto);
        User owner = getUser(userId);
        Category category = getCategory(newEventDto.getCategory());
        Event newEvent = eventMapper.toEvent(newEventDto, category);
        newEvent.setInitiator(owner);
        Location location = locationRepository.save(locationMapper.toLocation(newEventDto.getLocation()));
        newEvent.setLocation(location);
        newEvent.setCreatedOn(LocalDateTime.now());
        newEvent.setState(EventState.PENDING);
        Event event = eventRepository.save(newEvent);
        event.setViews(0L);
        event.setConfirmedRequests(0L);
        log.debug("Event is added {}", event);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.debug("Getting event ID{} by user ID{}", eventId, userId);
        getUser(userId);
        Event event = getEvent(eventId);
        event = fillWithEventViews(List.of(event)).get(0);
        event = fillWithConfirmedRequests(List.of(event)).get(0);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEvents(Long userId, PageRequest pageRequest) {
        log.debug("Getting events by user ID{}", userId);
        getUser(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        events = fillWithEventViews(events);
        events = fillWithConfirmedRequests(events);
        return eventMapper.toEventShortDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventDto updateEventDto, Long userId, Long eventId) {
        log.debug("Updating event ID{}", eventId);
        getUser(userId);
        Event event = getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("The event can only be changed by the owner");
        }
        Event updatedEvent = checkEventBeforeUpdate(event, updateEventDto);
        if (updateEventDto.getStateAction() != null) {
            switch (updateEventDto.getStateAction()) {
                case CANCEL_REVIEW:
                    updatedEvent.setState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    updatedEvent.setState(EventState.PENDING);
                    break;
                default:
                    throw new ConflictException("Incorrect state action");
            }
        }
        event = eventRepository.save(updatedEvent);
        event.setViews(0L);
        event.setConfirmedRequests(0L);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByEventId(Long userId, Long eventId) {
        log.debug("Getting requests of event ID{}", eventId);
        getUser(userId);
        getEvent(eventId);
        return requestMapper.toRequestDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public RequestResultDto updateRequestsStatus(RequestRequestDto requestUpdateDto, Long userId, Long eventId) {
        log.debug("Updating requests {}", requestUpdateDto.getRequestIds());
        getUser(userId);
        Event event = getEvent(eventId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0)
            throw new ConflictException("There is no need to update requests, " +
                    "due to the unlimited number of participants or moderation is off");
        Long numOfConfirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit().equals(numOfConfirmedRequests))
            throw new ConflictException("Limit of participant is reached");
        RequestResultDto requestResultDto = RequestResultDto
                .builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(requestUpdateDto.getRequestIds());
        for (Request request : requestsToUpdate) {
            if (event.getParticipantLimit().equals(numOfConfirmedRequests)) break;
            RequestDto requestDto;
            switch (requestUpdateDto.getStatus()) {
                case REJECTED:
                    request.setStatus(RequestStatus.REJECTED);
                    requestDto = requestMapper.toRequestDto(requestRepository.save(request));
                    requestResultDto.getRejectedRequests().add(requestDto);
                    break;
                case CONFIRMED:
                    request.setStatus(RequestStatus.CONFIRMED);
                    requestDto = requestMapper.toRequestDto(requestRepository.save(request));
                    requestResultDto.getConfirmedRequests().add(requestDto);
                    numOfConfirmedRequests++;
                    break;
                default:
                    throw new ConflictException("Wrong request status");
            }
        }
        return requestResultDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<Long> usersIds, List<EventState> states,
                                               List<Long> categoriesIds, LocalDateTime start, LocalDateTime end,
                                               PageRequest pageRequest) {
        log.debug("Getting events by admin");
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(states.isEmpty() ? null : stateInBuild(states));
        specifications.add(usersIds.isEmpty() ? null : userIdInBuild(usersIds));
        specifications.add(categoriesIds.isEmpty() ? null : categoryIdInBuild(categoriesIds));
        specifications.add(start == null ? null : eventDateAfterBuild(start));
        specifications.add(end == null ? null : eventDateBeforeBuild(end));
        specifications = specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<Event> events = eventRepository.findAll(specifications
                .stream()
                .reduce(Specification::and)
                .orElse(null), pageRequest).toList();
        events = fillWithConfirmedRequests(events);
        events = fillWithEventViews(events);
        return events.stream()
                .map(eventMapper::toEventFullDto)
                .sorted(Comparator.comparing(EventFullDto::getId,
                        Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(UpdateEventDto updateEventDto, Long eventId) {
        log.debug(String.format("Updating event ID%d by admin", eventId));
        Event event = checkEventBeforeUpdate(getEvent(eventId), updateEventDto);
        if (event.getState().equals(EventState.CANCELED)) throw new ConflictException("Not possible to update canceled event");
        if (updateEventDto.getStateAction() != null) {
            switch (updateEventDto.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(EventState.PUBLISHED);
                    break;
                default:
                    throw new ConflictException("Uncorrect state action");
            }
        }
        Event updatedEvent = eventRepository.save(event);
        event.setViews(0L);
        event.setConfirmedRequests(0L);
        log.debug(String.format("Event ID%d updated  by admin %s", eventId, updatedEvent));
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime start, LocalDateTime end,
                                                  Boolean onlyAvailable, EventSort sort, PageRequest pageRequest,
                                                  HttpServletRequest request) {
        log.debug("Getting published events by parameters");
        addStats(request);
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(stateInBuild(List.of(EventState.PUBLISHED)));
        specifications.add(text.isBlank() ? null : annotationAndDescriptionBuild(text));
        specifications.add(categories.isEmpty() ? null : categoryIdInBuild(categories));
        specifications.add(paid == null ? null : paidIsBuild(paid));
        specifications.add(start == null ? null : eventDateAfterBuild(start));
        specifications.add(end == null ? null : eventDateBeforeBuild(end));
        specifications = specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Specification<Event> s = specifications
                .stream()
                .reduce(Specification::and).orElse(null);

        List<Event> events = eventRepository.findAll(s, pageRequest).toList();
        events = fillWithConfirmedRequests(events);
        events = onlyAvailable ? events
                .stream()
                .filter(event -> event.getParticipantLimit() > event.getConfirmedRequests())
                .collect(Collectors.toList()) : events;
        events = fillWithEventViews(events);
        if (sort == null) return eventMapper.toEventShortDto(events);
        switch (sort) {
            case VIEWS:
                return events
                        .stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .map(eventMapper::toEventShortDto)
                        .collect(Collectors.toList());
            case EVENT_DATE:
                return events
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .map(eventMapper::toEventShortDto)
                        .collect(Collectors.toList());
            default:
                return eventMapper.toEventShortDto(events);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublishedEventById(Long id, HttpServletRequest request) {
        log.debug("Getting published event ID{}", id);
        Event event = getEvent(id);
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new NotFoundException(String.format("Event ID%d not published yet", id));
        addStats(request);
        event = fillWithEventViews(List.of(event)).get(0);
        event = fillWithConfirmedRequests(List.of(event)).get(0);
        return eventMapper.toEventFullDto(event);
    }

    private void addStats(HttpServletRequest request) {
        statsClient.addStat(HitsDto.builder()
                .app("explore-with-me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    public List<Event> fillWithEventViews(List<Event> events) {
        String eventsUri = "/events" + "/";
        List<String> uris = events
                .stream()
                .map(event -> String.format(eventsUri + "%d", event.getId()))
                .collect(Collectors.toList());
        List<LocalDateTime> startDates = events
                .stream()
                .map(Event::getCreatedOn)
                .sorted()
                .collect(Collectors.toList());
        if (startDates.stream().findFirst().isEmpty()) return events;
        List<StatsDto> viewStatsList = statsClient.getStats(startDates.stream().findFirst().get(), LocalDateTime.now(),
                uris, true);
        Map<Long, Long> mapEventIdViews = viewStatsList
                .stream()
                .collect(Collectors.toMap(
                        statsDto -> Long.parseLong(statsDto.getUri().substring(eventsUri.length())),
                        StatsDto::getHits));
        return events
                .stream()
                .peek(event -> event.setViews(mapEventIdViews.getOrDefault(event.getId(), 1L)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> fillWithConfirmedRequests(List<Event> events) {
        List<Request> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList()), RequestStatus.CONFIRMED);
        Map<Long, List<Request>> mapEventIdConfirmedRequests = confirmedRequests
                .stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()));
        return events
                .stream()
                .peek(event -> event.setConfirmedRequests((long) mapEventIdConfirmedRequests
                        .getOrDefault(event.getId(), new ArrayList<>()).size()))
                .collect(Collectors.toList());
    }

    private Event checkEventBeforeUpdate(Event event, UpdateEventDto updateEventDto) {
        if (event.getState().equals(EventState.PUBLISHED))
            throw new ConflictException("Published events cannot be changed");
        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation()
                .equals(event.getAnnotation())) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null && !updateEventDto.getCategory()
                .equals(event.getCategory().getId())) {
            event.setCategory(getCategory(updateEventDto.getCategory()));
        }
        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription()
                .equals(event.getDescription())) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null && !updateEventDto.getEventDate()
                .equals(event.getEventDate())) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(locationRepository.save(locationMapper.toLocation(updateEventDto.getLocation())));
        }
        if (updateEventDto.getPaid() != null && !updateEventDto.getPaid().equals(event.getPaid())) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null && !updateEventDto.getTitle().equals(event.getTitle())) {
            event.setTitle(updateEventDto.getTitle());
        }
        return event;
    }

    private Specification<Event> userIdInBuild(List<Long> usersIds) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator").get("id")).value(usersIds));
    }

    private Specification<Event> stateInBuild(List<EventState> states) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states));
    }

    private Specification<Event> categoryIdInBuild(List<Long> categoriesIds) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id"))
                .value(categoriesIds));
    }

    private Specification<Event> eventDateAfterBuild(LocalDateTime rangeStart) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
    }

    private Specification<Event> eventDateBeforeBuild(LocalDateTime rangeEnd) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
    }

    private Specification<Event> annotationAndDescriptionBuild(String text) {
        String searchText = "%" + text.toLowerCase() + "%";
        return ((root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchText),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchText)));
    }

    private Specification<Event> paidIsBuild(Boolean paid) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User ID%d not found", userId)));
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format("Category ID%d not found", categoryId)));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event ID%d not found", eventId)));
    }
}
