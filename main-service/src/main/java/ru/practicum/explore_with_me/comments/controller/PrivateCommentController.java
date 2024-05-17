package ru.practicum.explore_with_me.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.comments.dto.CommentFullDto;
import ru.practicum.explore_with_me.comments.dto.NewCommentDto;
import ru.practicum.explore_with_me.comments.dto.UpdateCommentDto;
import ru.practicum.explore_with_me.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto addComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                     @PathVariable @Positive Long userId) {
        log.info("Response from POST request on {}", "/users/{userId}/comments");
        return commentService.addComment(newCommentDto, userId);
    }

    @PatchMapping
    public CommentFullDto updateComment(@RequestBody @Valid UpdateCommentDto updatedCommentDto,
                                        @PathVariable @Positive Long userId) {
        log.info("Response from PATCH request on {}", "/users/{userId}/comments");
        return commentService.updateComment(updatedCommentDto, userId);
    }

    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long comId) {
        log.info("Response from DELETE request on {}", "/users/{userId}/comments" + "/{comId}");
        commentService.deleteComment(userId, comId);
    }
}
