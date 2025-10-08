package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectCountException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FilmService {
    private final @Qualifier("filmDbStorage") FilmStorage filmStorage;
    private final @Qualifier("userDbStorage") UserStorage userStorage;
    private final @Qualifier("filmDbStorage") FilmDbStorage filmDbStorage;

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
        if (this.userStorage.getUser(userId).isEmpty()) {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        filmDbStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        if (this.userStorage.getUser(userId).isEmpty()) {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        filmDbStorage.removeLike(filmId, userId);
    }

    public Set<Film> findTopCountByLikes(int count) {
        log.info("Запрос топ-{} фильмов по количеству лайков", count);
        if (count <= 0){
            throw new IncorrectCountException("Значение переменной count должно быть > 0");
        }

        List<Film> topFilms = filmDbStorage.getTopFilmsByLikes(count);
        return new LinkedHashSet<>(topFilms);
    }

}
