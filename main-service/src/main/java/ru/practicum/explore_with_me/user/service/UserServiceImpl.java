package ru.practicum.explore_with_me.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.error.exeption.NotFoundException;
import ru.practicum.explore_with_me.user.dto.NewUserDto;
import ru.practicum.explore_with_me.user.dto.UserDto;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addUser(NewUserDto newUserDTO) {
        log.debug("Adding user {}", newUserDTO);
        User user = userRepository.save(userMapper.toUser(newUserDTO));
        log.debug("User is added {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Deleting user ID{}", userId);
        if (!userRepository.existsById(userId)) throw new NotFoundException(String.format("User ID%d not found", userId));
        userRepository.deleteById(userId);
        log.debug("User ID{} is deleted", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, PageRequest pageRequest) {
        log.debug("Getting list of users");
        return (ids.isEmpty()) ? userMapper.toUserDto(userRepository.findAll(pageRequest).toList())
                : userMapper.toUserDto(userRepository.findAllByIdIn(ids, pageRequest));
    }
}
