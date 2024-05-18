package ru.practicum.explore_with_me.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore_with_me.comments.dto.CommentFullDto;
import ru.practicum.explore_with_me.comments.dto.CommentShortDto;
import ru.practicum.explore_with_me.comments.dto.NewCommentDto;
import ru.practicum.explore_with_me.comments.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentFullDto addComment(NewCommentDto newCommentDto, Long userId);

    CommentFullDto updateComment(UpdateCommentDto newCommentDto, Long userId);

    void deleteComment(Long userId, Long comId);

    List<CommentFullDto> getCommentsByAuthorId(Long userId, PageRequest pageRequest);

    void deleteCommentByAdmin(Long comId);

    CommentFullDto getComment(Long comId);

    List<CommentShortDto> getCommentsByEventId(Long eventId, PageRequest pageRequest);
}
