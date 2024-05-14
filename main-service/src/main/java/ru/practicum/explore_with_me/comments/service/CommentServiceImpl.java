package ru.practicum.explore_with_me.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.comments.model.Comment;
import ru.practicum.explore_with_me.comments.dto.CommentFullDto;
import ru.practicum.explore_with_me.comments.dto.CommentShortDto;
import ru.practicum.explore_with_me.comments.dto.NewCommentDto;
import ru.practicum.explore_with_me.comments.mapper.CommentMapper;
import ru.practicum.explore_with_me.comments.repository.CommentRepository;
import ru.practicum.explore_with_me.error.exeption.ConflictException;
import ru.practicum.explore_with_me.error.exeption.NotFoundException;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explore_with_me.enums.EventState.PUBLISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentFullDto addComment(NewCommentDto newCommentDTO, Long userId, Long eventId) {
        log.debug("Add comment to event ID{} from user ID{} {}", eventId, userId, newCommentDTO);
        User user = getUserIfPresent(userId);
        Event event = getEventIfPresent(eventId);
        if (!PUBLISHED.equals(event.getState()))
            throw new ConflictException("You can only add comments to published events");
        Comment comment = Comment.builder()
                .text(newCommentDTO.getText())
                .author(user)
                .event(event)
                .createTime(LocalDateTime.now())
                .editTime(null)
                .build();
        CommentFullDto fullCommentDto = commentMapper.toCommentFullDto(commentRepository.save(comment));
        log.debug("Add comment {}", fullCommentDto);
        return fullCommentDto;
    }

    @Override
    @Transactional
    public CommentFullDto updateComment(NewCommentDto newCommentDto, Long userId, Long comId) {
        log.debug("Updating comment ID{} {}", userId, newCommentDto);
        getUserIfPresent(userId);
        Comment comment = getCommentIfPresent(comId);
        if (!userId.equals(comment.getAuthor().getId()))
            throw new ConflictException("Only author can update the comment");
        comment.setText(newCommentDto.getText());
        comment.setEditTime(LocalDateTime.now());
        CommentFullDto fullCommentDto = commentMapper.toCommentFullDto(commentRepository.save(comment));
        log.debug("Comment updated {}", fullCommentDto);
        return fullCommentDto;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long comId) {
        log.debug("Deleting comment ID{}", comId);
        getUserIfPresent(userId);
        Comment comment = getCommentIfPresent(comId);
        if (!userId.equals(comment.getAuthor().getId()))
            throw new ConflictException("Only author can delete comment");
        commentRepository.deleteById(comId);
        log.debug("Comment deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentFullDto> getCommentsByAuthorId(Long userId, PageRequest pageRequest) {
        log.debug("Get list of comments with userID{}", userId);
        getUserIfPresent(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageRequest);
        return commentMapper.toCommentFullDto(comments);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long comId) {
        log.debug("Delete commentID{} by admin", comId);
        getCommentIfPresent(comId);
        commentRepository.deleteById(comId);
        log.debug("Comment deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public CommentFullDto getComment(Long comId) {
        log.debug("Get comment with ID{}", comId);
        Comment comment = getCommentIfPresent(comId);
        return commentMapper.toCommentFullDto(comment);
    }

    @Override
    @Transactional
    public List<CommentShortDto> getCommentsByEventId(Long eventId, PageRequest pageRequest) {
        log.debug("Get list of comments with eventID{}", eventId);
        getEventIfPresent(eventId);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageRequest);
        return commentMapper.toCommentShortDto(comments);
    }

    private User getUserIfPresent(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User ID%d not found", userId)));
    }

    private Comment getCommentIfPresent(Long comId) {
        return commentRepository.findById(comId).orElseThrow(() ->
                new NotFoundException(String.format("Comment ID%d not found", comId)));

    }

    private Event getEventIfPresent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event ID%d not found", eventId)));
    }

}
