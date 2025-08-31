package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final HashMap<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        FilmValidator.validate(film);
        film.setId(nextId++);

        log.debug("У добавляемого фильма id: {}", film.getId());
        this.films.put(film.getId(), film);

        log.info("Фильм '{}' успешно создан с id={}", film.getName(), film.getId());

        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(this.films.values());
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        FilmValidator.validate(film);
        if (!films.containsKey(film.getId()))
            throw new ValidationException("Фильм с таким id не найден");

        this.films.put(film.getId(), film);
        log.info("Фильм '{}' успешно обновлён", film.getName());
        return this.films.get(film.getId());
    }
}