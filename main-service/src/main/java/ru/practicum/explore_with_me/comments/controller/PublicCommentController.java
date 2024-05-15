package ru.practicum.explore_with_me.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.comments.dto.CommentFullDto;
import ru.practicum.explore_with_me.comments.dto.CommentShortDto;
import ru.practicum.explore_with_me.comments.service.CommentService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/comments" + "/{comId}")
    public CommentFullDto getComment(@PathVariable @Positive Long comId) {
        log.info("Response from GET request on {}", "/comments" + "/{comId}");
        return commentService.getComment(comId);
    }

    @GetMapping("/events" + "/{eventId}" + "/comments")
    public List<CommentShortDto> getCommentsByEventId(@PathVariable @Positive Long eventId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Response from GET request on {}", "/events" + "/{eventId}" + "/comments");
        return commentService.getCommentsByEventId(eventId, PageRequest.of(from / size, size));
    }
}
