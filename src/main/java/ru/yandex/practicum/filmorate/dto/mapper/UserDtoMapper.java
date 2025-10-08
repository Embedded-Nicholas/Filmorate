package ru.yandex.practicum.filmorate.dto.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dto.UserCreateRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.UserUpdateRequest;

@Component
public class UserDtoMapper {
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friendsCount(user.getFriendsCount())
                .build();
    }

    public User fromCreate(UserCreateRequest request) {
        return User.builder()
                .email(request.getEmail())
                .login(request.getLogin())
                .name(request.getName())
                .birthday(request.getBirthday())
                .build();
    }

    public User fromUpdate(UserUpdateRequest request) {
        return User.builder()
                .id(request.getId())
                .email(request.getEmail())
                .login(request.getLogin())
                .name(request.getName())
                .birthday(request.getBirthday())
                .build();
    }
}


