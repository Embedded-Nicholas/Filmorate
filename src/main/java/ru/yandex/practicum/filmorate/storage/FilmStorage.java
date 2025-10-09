package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {
    Film addFilm(Film film);
    boolean removeFilm(Long id);
    Film updateFilm(Film film);
    Optional<Film> getFilm(Long id);
    Map<Long, Film> getFilms();
}
