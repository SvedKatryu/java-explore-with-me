package ru.practicum.explore_with_me.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private String email;
    private int id;
    private String name;
}
