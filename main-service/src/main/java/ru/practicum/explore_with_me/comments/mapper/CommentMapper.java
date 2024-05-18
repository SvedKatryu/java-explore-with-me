package ru.practicum.explore_with_me.comments.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.comments.model.Comment;
import ru.practicum.explore_with_me.comments.dto.CommentFullDto;
import ru.practicum.explore_with_me.comments.dto.CommentShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentFullDto toCommentFullDto(Comment comment);

    List<CommentFullDto> toCommentFullDto(List<Comment> comment);

    CommentShortDto toCommentShortDto(Comment comment);

    List<CommentShortDto> toCommentShortDto(List<Comment> comment);
}
