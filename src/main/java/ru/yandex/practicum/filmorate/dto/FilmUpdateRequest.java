package ru.yandex.practicum.filmorate.dto;

import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class FilmUpdateRequest {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Long duration;
    Long mpa;
    List<Long> genres;
}

