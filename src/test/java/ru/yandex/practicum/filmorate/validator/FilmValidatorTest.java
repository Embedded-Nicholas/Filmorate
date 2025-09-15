package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    @Test
    void validate_validFilm_shouldNotThrow() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120L)
                .build();

        assertDoesNotThrow(() -> FilmValidator.validate(film));
    }

    @Test
    void validate_emptyName_shouldThrow() {
        Film film = Film.builder()
                .name("")
                .description("Описание")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120L)
                .build();

        ValidationException ex = assertThrows(ValidationException.class,
                () -> FilmValidator.validate(film));
        assertEquals("Название не может быть пустым", ex.getMessage());
    }

    @Test
    void validate_tooLongDescription_shouldThrow() {
        String longDesc = "a".repeat(201);
        Film film = Film.builder()
                .name("Film")
                .description(longDesc)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100L)
                .build();

        ValidationException ex = assertThrows(ValidationException.class,
                () -> FilmValidator.validate(film));
        assertEquals("Максимальная длина описания — 200 символов", ex.getMessage());
    }

    @Test
    void validate_releaseDateBeforeCinemaBirthday_shouldThrow() {
        Film film = Film.builder()
                .name("Film")
                .description("Desc")
                .releaseDate(LocalDate.of(1800, 1, 1))
                .duration(100L)
                .build();

        ValidationException ex = assertThrows(ValidationException.class,
                () -> FilmValidator.validate(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void validate_negativeDuration_shouldThrow() {
        Film film = Film.builder()
                .name("Film")
                .description("Desc")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(-10L)
                .build();

        ValidationException ex = assertThrows(ValidationException.class,
                () -> FilmValidator.validate(film));
        assertEquals("Продолжительность фильма должна быть положительным числом", ex.getMessage());
    }
}