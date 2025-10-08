package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class UserDto {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Integer friendsCount;
}


