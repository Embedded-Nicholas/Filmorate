package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    @Setter
    private Long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;
}