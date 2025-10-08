package ru.yandex.practicum.filmorate.dto.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.enums.Genre;
import ru.yandex.practicum.filmorate.enums.MpaRating;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmDtoMapper {
    public FilmDto toDto(Film film) {
        Set<String> genres = film.getFilmGenres() == null ? null : film.getFilmGenres().stream()
                .map(Genre::name)
                .collect(Collectors.toSet());
        Integer likesCount = film.getLikedUserIds() == null ? 0 : film.getLikedUserIds().size();
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpaCode(film.getMpaRating() == null ? null : film.getMpaRating().getCode())
                .genres(genres)
                .likesCount(likesCount)
                .build();
    }

    public Film fromCreate(FilmCreateRequest request) {
        return Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .build();
    }

    public Film fromUpdate(FilmUpdateRequest request) {
        return Film.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .build();
    }
}


