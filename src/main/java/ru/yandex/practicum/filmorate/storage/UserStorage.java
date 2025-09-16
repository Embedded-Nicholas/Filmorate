package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);
    boolean removeUser(User user);
    User updateUser(User user);
    Optional<User> getUser(Long id);
    Map<Long, User> getUsers();
//    Set<User> getFriends(Long id);
//    Set<User> getCommonFriends(Long userId1, Long userId2);

}
