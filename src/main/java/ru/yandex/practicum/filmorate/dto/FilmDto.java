package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
public class FilmDto {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Long duration;
    String mpaCode;
    Set<String> genres;
    Integer likesCount;
}


