package ru.yandex.practicum.filmorate.dto;

import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class FilmCreateRequest {
    String name;
    String description;
    LocalDate releaseDate;
    Long duration;
    Ref mpa;
    List<Ref> genres;

    @Value
    public static class Ref { Long id; }
}
