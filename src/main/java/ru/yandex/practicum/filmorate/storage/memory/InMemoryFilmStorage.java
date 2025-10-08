package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Repository("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(nextId++);
        film.setLikedUserIds(new HashSet<>());
        films.put(film.getId(), film);
        log.debug("Фильм добавлен в хранилище: {}", film);
        return film;
    }

    @Override
    public boolean removeFilm(Long id) {
        boolean removed = films.remove(id) != null;
        log.debug("Фильм с id={} удален из хранилища: {}", id, removed);
        return removed;
    }

    @Override
    public Film updateFilm(Film film) {
        this.films.put(film.getId(), film);
        log.debug("Фильм обновлён в хранилище: {}", film);
        return film;
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        return Optional.ofNullable(this.films.get(id));
    }

    @Override
    public HashMap<Long, Film> getFilms() {
        return this.films;
    }
}
