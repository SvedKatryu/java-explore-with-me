package ru.practicum.explore_with_me.request.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.request.Request;
import ru.practicum.explore_with_me.request.dto.RequestDto;
import ru.practicum.explore_with_me.user.model.User;

public abstract class RequestMapperDecorator implements RequestMapper {
    @Autowired
    private RequestMapper requestMapper;

    @Override
    public Request toRequest(RequestDto requestDto, Event event, User requester) {
        Request request = requestMapper.toRequest(requestDto, event, requester);
        request.setEvent(event);
        request.setRequester(requester);
        return request;
    }
}
