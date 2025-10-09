package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);
    boolean removeUser(User user);
    User updateUser(User user);
    Optional<User> getUser(Long id);
    Map<Long, User> getUsers();
}
