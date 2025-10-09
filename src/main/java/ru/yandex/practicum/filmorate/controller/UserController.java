package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dto.UserCreateRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateRequest;
import ru.yandex.practicum.filmorate.dto.mapper.UserDtoMapper;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping
    public UserDto addUser(@RequestBody UserCreateRequest request) {
        User user = userDtoMapper.fromCreate(request);
        return userDtoMapper.toDto(this.userService.addUser(user));
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable String id) {
        Optional<User> user = this.userService.getUser(Long.parseLong(id));
        return user.map(userDtoMapper::toDto).orElse(null);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable String id, @PathVariable String friendId) {
        this.userService.addFriend(Long.parseLong(id), Long.parseLong(friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId} ")
    public void deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        this.userService.removeFriend(Long.parseLong(id), Long.parseLong(friendId));
    }

    @GetMapping("/{id}/friends")
    public Set<UserDto> getFriends(@PathVariable String id) {
        Set<User> friends = this.userService.getFriends(Long.parseLong(id));
        Set<UserDto> dtos = new HashSet<>();
        for (User u : friends) {
            dtos.add(userDtoMapper.toDto(u));
        }
        return dtos;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<UserDto> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        Set<User> friends = this.userService.getCommonFriends(Long.parseLong(id), Long.parseLong(otherId));
        Set<UserDto> dtos = new HashSet<>();
        for (User u : friends) {
            dtos.add(userDtoMapper.toDto(u));
        }
        return dtos;
    }

    @GetMapping
    public Set<UserDto> getAllUsers() {
        Set<User> users = this.userService.getUsers();
        Set<UserDto> dtos = new HashSet<>();
        for (User u : users) {
            dtos.add(userDtoMapper.toDto(u));
        }
        return dtos;
    }

    @PutMapping
    public UserDto updateUser(@RequestBody UserUpdateRequest request) {
        User user = userDtoMapper.fromUpdate(request);
        return userDtoMapper.toDto(this.userService.updateUser(user));
    }
}