package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserService {
    private final UserStorage storage;

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
        this.manageFriends(userId, friendId,
                (user, friend) -> {
                    user.addFriend(friend.getId());
                    friend.addFriend(user.getId());
                });
    }

    public Set<User> getFriends(Long userId) {
        Optional<User> user = this.getUser(userId);
        Set<User> friends = getFriendsFromUserIds(user);
        return friends;
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь {} удаляет из друзей пользователя {}", userId, friendId);
        this.manageFriends(userId, friendId,
                (user, friend) -> {
                    user.removeFriend(friend.getId());
                    friend.removeFriend(user.getId());
                });
    }

    public Set<User> getCommonFriends(Long userId1, Long userId2) {
        log.info("Поиск общих друзей у пользователей с id: {}, {}", userId1, userId2);
        Optional<User> user1 = this.getUser(userId1);
        Optional<User> user2 = this.getUser(userId2);

        if (user1.isPresent() && user2.isPresent()) {
            Set<User> user1Friends = this.getFriendsFromUserIds(user1);
            Set<User> user2Friends = this.getFriendsFromUserIds(user2);

            Set<User> commonFriends = new HashSet<>(user1Friends);
            commonFriends.retainAll(user2Friends);
            log.debug("Запрос общих друзей пользователя c id: {} и пользователя с id: {}, количество общих друзей={}", userId1, userId2, commonFriends.size());
            return commonFriends;
        }
        return Collections.emptySet();
    }

    private Set<User> getFriendsFromUserIds(Optional<User> user) {
        return user.map(value -> value.getFriends()
                .stream()
                .map(this::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toSet())).orElse(Collections.emptySet());
    }

    private void manageFriends(Long userId,
                               Long friendId,
                               BiConsumer<User, User> function) {
        Optional<User> user = storage.getUser(userId);
        Optional<User> friend = storage.getUser(friendId);

        if (user.isPresent() && friend.isPresent()) {
            function.accept(user.get(), friend.get());
        } else {
            throw new ObjectNotFoundException("Одного из друзей не существует");
        }
    }
}
