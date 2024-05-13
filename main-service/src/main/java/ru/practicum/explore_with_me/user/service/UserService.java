package ru.practicum.explore_with_me.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore_with_me.user.dto.NewUserDto;
import ru.practicum.explore_with_me.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserDto newUserDto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, PageRequest pageRequest);
}
