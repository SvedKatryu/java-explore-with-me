package ru.practicum.explore_with_me.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.explore_with_me.user.dto.UserShortDto;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentShortDto {
    private long id;
    private String text;
    private UserShortDto author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String editTime;
}
