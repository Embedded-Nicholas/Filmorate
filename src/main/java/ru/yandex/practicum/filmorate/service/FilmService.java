package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectCountException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        FilmValidator.validate(film);
        if (!this.filmStorage.getFilms().containsKey(film.getId())) {
            log.info("Добавление фильма: {}", film);
            this.filmStorage.addFilm(film);
        } else {
            throw new IllegalArgumentException("Фильм уже существует");
        }
        return film;
    }

    public void removeFilm(Film film) {
        FilmValidator.validate(film);
        log.info("Удаление фильма: {}", film);
        boolean removed = this.filmStorage.removeFilm(film.getId());
        if (removed) {
            log.debug("Фильм удалён из хранилища: {}", film);
        } else {
            throw new ObjectNotFoundException("Такого фильма не существует");
        }
    }

    public Film updateFilm(Film film) {
        FilmValidator.validate(film);
        log.info("Обновление фильма: {}", film);
        if (!this.filmStorage.getFilms().containsKey(film.getId())) {
            throw new ObjectNotFoundException("Такого фильма не существует");
        }
        return this.filmStorage.updateFilm(film);
    }

    public Optional<Film> getFilm(Long id) {
        log.info("Запрос фильма по id={}", id);
        Optional<Film> film = this.filmStorage.getFilm(id);
        if (film.isEmpty()) {
            throw new ObjectNotFoundException("Такого фильма не существует");
        }
        return film;
    }

    public Set<Film> getFilms() {
        log.info("Запрос списка всех фильмов");
        return new HashSet<>(this.filmStorage.getFilms().values());
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        this.manageLikes(filmId, userId, film -> film.addLike(userId));
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        this.manageLikes(filmId, userId, film -> film.removeLike(userId));
    }

    public Set<Film> findTopCountByLikes(int count) {
        log.info("Запрос топ-10 фильмов по количеству лайков");
        if (count <= 0){
            throw new IncorrectCountException("Значение переменной count должно быть > 0");
        }

        return this.filmStorage.getFilms().values().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void manageLikes(Long filmId, Long userId, Consumer<Film> function) {
        if (this.userStorage.getUser(userId).isEmpty()){
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }

        Optional<Film> film = this.filmStorage.getFilm(filmId);
        if (film.isPresent()) {
            function.accept(film.get());
        } else {
            throw new ObjectNotFoundException("Такого фильма не существует");
        }
    }
}
