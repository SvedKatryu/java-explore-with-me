package ru.practicum.explore_with_me.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCommentDto {
    @NotBlank
    @Size(min = 1, max = 512)
    private String text;
}
