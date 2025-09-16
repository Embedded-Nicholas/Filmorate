package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public User addUser(User user) {
        UserValidator.validate(user);
        user.setId(nextId++);
        user.setFriends(new HashSet<>());
        user.setFriendsCount(0);
        this.users.put(user.getId(), user);
        log.debug("Пользователь добавлен в хранилище: {}", user);
        return user;
    }

    @Override
    public boolean removeUser(User user) {
        return this.users.remove(user.getId()) != null;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь обновлён в хранилище: {}", user);
        return user;
    }

    @Override
    public Optional<User> getUser(Long id) {
        Optional<User> user = Optional.ofNullable(this.users.get(id));
        log.debug("Запрос пользователя по id={}, найден: {}", id, user.isPresent());
        return user;
    }

    @Override
    public Map<Long, User> getUsers() {
        log.debug("Запрос всех пользователей, количество={}", users.size());
        return this.users;
    }
}
