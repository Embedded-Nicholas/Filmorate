package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;
}