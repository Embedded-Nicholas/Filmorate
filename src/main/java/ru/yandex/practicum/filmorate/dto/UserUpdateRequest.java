package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.time.LocalDate;

@Value
public class UserUpdateRequest {
    @NotNull
    Long id;

    @NotBlank
    @Email
    String email;

    @NotBlank
    String login;

    String name;

    @NotNull
    LocalDate birthday;
}


