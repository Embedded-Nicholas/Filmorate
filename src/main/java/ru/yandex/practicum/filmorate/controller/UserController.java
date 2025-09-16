package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public User addUser(@RequestBody User user) {
        return this.userService.addUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        Optional<User> user = this.userService.getUser(Long.parseLong(id));
        return user.orElse(null);
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
    public Set<User> getFriends(@PathVariable String id) {
        return this.userService.getFriends(Long.parseLong(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        return this.userService.getCommonFriends(Long.parseLong(id), Long.parseLong(otherId));
    }

    @GetMapping
    public Set<User> getAllUsers() {
        return this.userService.getUsers();
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return this.userService.updateUser(user);
    }
}