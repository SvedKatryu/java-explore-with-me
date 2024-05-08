package ru.practicum.explore_with_me.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.user.dto.NewUserDto;
import ru.practicum.explore_with_me.user.dto.UserDto;
import ru.practicum.explore_with_me.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(NewUserDto newUserDTO);

    UserDto toUserDto(User user);

    List<UserDto> toUserDto(List<User> users);
}
