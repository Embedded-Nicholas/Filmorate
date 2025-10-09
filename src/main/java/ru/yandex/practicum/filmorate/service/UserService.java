package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserService {
    private final @Qualifier("userDbStorage") UserStorage storage;
    private final @Qualifier("userDbStorage") UserDbStorage userDbStorage;

    public User addUser(User user) {
        log.info("Добавление пользователя: {}", user);
        UserValidator.validate(user);

        User addedUser = this.storage.addUser(user);

        if (addedUser != null) {
            log.debug("Пользователь добавлен в хранилище: {}", addedUser);
            return addedUser;
        } else {
            throw new IllegalArgumentException("Пользователь уже существует");
        }
    }

    public boolean removeUser(User user) {
        log.info("Удаление пользователя: {}", user);
        UserValidator.validate(user);
        boolean removed = storage.removeUser(user);
        if (!removed) {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        log.debug("Пользователь удалён из хранилища: {}", user);
        return true;
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя: {}", user);
        UserValidator.validate(user);
        if (!this.storage.getUsers().containsKey(user.getId())) {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        return storage.updateUser(user);
    }

    public Optional<User> getUser(Long id) {
        log.debug("Запрос пользователя по id={}", id);
        Optional<User> user = this.storage.getUser(id);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        return user;
    }

    public Set<User> getUsers() {
        log.info("Запрос списка всех пользователей");
        return new HashSet<>(storage.getUsers().values());
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        userDbStorage.addFriend(userId, friendId);
    }

    public Set<User> getFriends(Long userId) {
        List<User> friends = userDbStorage.getFriends(userId);
        return new HashSet<>(friends);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь {} удаляет из друзей пользователя {}", userId, friendId);
        userDbStorage.removeFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(Long userId1, Long userId2) {
        log.info("Поиск общих друзей у пользователей с id: {}, {}", userId1, userId2);
        List<User> commonFriends = userDbStorage.getCommonFriends(userId1, userId2);
        log.debug("Запрос общих друзей пользователя c id: {} и пользователя с id: {}, количество общих друзей={}", userId1, userId2, commonFriends.size());
        return new HashSet<>(commonFriends);
    }

}
