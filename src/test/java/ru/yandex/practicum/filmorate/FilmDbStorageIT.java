package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.enums.MpaRating;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageIT {
    private final FilmDbStorage filmStorage;

    @Test
    void testAddAndGetFilm() {
        Film film = Film.builder()
                .name("IT Film")
                .description("desc")
                .releaseDate(LocalDate.of(2020,1,1))
                .duration(100L)
                .mpaRating(MpaRating.G)
                .build();
        Film saved = filmStorage.addFilm(film);
        Optional<Film> loaded = filmStorage.getFilm(saved.getId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("IT Film");
    }
}


