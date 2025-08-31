package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public User addUser(@RequestBody User user) {
        UserValidator.validate(user);

        user.setId(nextId++);
        users.put(user.getId(), user);

        log.debug("У добавляемого пользователя id: {}", user.getId());
        log.info("Пользователь '{}' успешно создан с id={}", user.getLogin(), user.getId());

        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        UserValidator.validate(user);

        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id не найден");
        }

        users.put(user.getId(), user);
        log.info("Пользователь '{}' успешно обновлён", user.getLogin());

        return user;
    }
}