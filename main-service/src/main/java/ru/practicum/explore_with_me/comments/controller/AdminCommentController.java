package ru.practicum.explore_with_me.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.comments.dto.CommentFullDto;
import ru.practicum.explore_with_me.comments.service.CommentService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/{userId}")
    public List<CommentFullDto> getCommentsByAuthorId(@PathVariable @Positive Long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Response from GET request on {}", "/admin/comments" + "/{userId}");
        return commentService.getCommentsByAuthorId(userId, PageRequest.of(from / size, size));
    }

    @DeleteMapping("/admin/comments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long comId) {
        log.info("Response from DELETE request on {}", "/admin/comments" + "/{userId}");
        commentService.deleteCommentByAdmin(comId);
    }
}
