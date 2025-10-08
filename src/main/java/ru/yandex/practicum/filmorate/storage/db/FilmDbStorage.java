package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.Genre;
import ru.yandex.practicum.filmorate.enums.MpaRating;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.db.util.SqlQueryLoader;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("filmDbStorage")
@Primary
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final SqlQueryLoader sqlQueryLoader;


    @Override
    public Film addFilm(Film film) {
        log.debug("Добавление фильма в БД: {}", film);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Add film");
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            Long mpaId = getMpaRatingId(film.getMpaRating());
            ps.setObject(5, mpaId);
            return ps;
        }, keyHolder);
        
        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);

        saveFilmGenres(film);
        
        log.debug("Фильм добавлен в БД с ID: {}", generatedId);
        return film;
    }

    @Override
    public boolean removeFilm(Long id) {
        log.debug("Удаление фильма из БД с ID: {}", id);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Remove film");
        int rowsAffected = jdbcTemplate.update(sql, id);
        
        boolean removed = rowsAffected > 0;
        log.debug("Фильм удален из БД: {}", removed);
        return removed;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновление фильма в БД: {}", film);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Update film");
        
        int rowsAffected = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                getMpaRatingId(film.getMpaRating()),
                film.getId());
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Фильм с ID " + film.getId() + " не найден");
        }

        updateFilmGenres(film);
        
        log.debug("Фильм обновлен в БД");
        return film;
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        log.debug("Получение фильма по ID: {}", id);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Get film by ID");
        
        try {
            Film film = jdbcTemplate.queryForObject(sql, this::mapFilmWithMpa, id);
            if (film != null) {
                initializeFilmCollections(film);
                loadFilmGenres(film);
                loadFilmLikes(film);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильм с ID {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, Film> getFilms() {
        log.debug("Получение всех фильмов из БД");
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Get all films");
        
        List<Film> films = jdbcTemplate.query(sql, this::mapFilmWithMpa);

        films.forEach(film -> {
            initializeFilmCollections(film);
            loadFilmGenres(film);
            loadFilmLikes(film);
        });
        
        Map<Long, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        
        log.debug("Получено фильмов: {}", filmMap.size());
        return filmMap;
    }

    private Film mapFilmWithMpa(ResultSet rs, int rowNum) throws SQLException {
        Film film = filmRowMapper.mapRow(rs, rowNum);

        if (!rs.wasNull() && rs.getLong("mpa_id") != 0) {
            String mpaCode = rs.getString("mpa_code");
            film.setMpaRating(MpaRating.getByName(mpaCode));
        }
        
        return film;
    }

    private void initializeFilmCollections(Film film) {
        film.setLikedUserIds(new HashSet<>());
        film.setFilmGenres(new HashSet<>());
    }

    private void loadFilmGenres(Film film) {
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Load film genres");
        
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            String genreName = rs.getString("name");
            return Genre.valueOf(genreName);
        }, film.getId());
        
        film.setFilmGenres(new HashSet<>(genres));
    }

    private void loadFilmLikes(Film film) {
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Load film likes");
        
        List<Long> likedUserIds = jdbcTemplate.queryForList(sql, Long.class, film.getId());
        film.setLikedUserIds(new HashSet<>(likedUserIds));
    }

    private void saveFilmGenres(Film film) {
        if (film.getFilmGenres() == null || film.getFilmGenres().isEmpty()) {
            return;
        }
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Save film genres");
        
        for (Genre genre : film.getFilmGenres()) {
            Long genreId = getGenreId(genre);
            jdbcTemplate.update(sql, film.getId(), genreId);
        }
    }

    private void updateFilmGenres(Film film) {
        String deleteSql = sqlQueryLoader.getQuery("film-queries.sql", "Update film genres (delete old)");
        jdbcTemplate.update(deleteSql, film.getId());

        saveFilmGenres(film);
    }

    private Long getMpaRatingId(MpaRating mpaRating) {
        if (mpaRating == null) {
            return null;
        }
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Get MPA rating ID");
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, mpaRating.getCode());
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("MPA рейтинг с кодом " + mpaRating.getCode() + " не найден");
        }
    }

    private Long getGenreId(Genre genre) {
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Get genre ID");
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, genre.name());
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Жанр " + genre.name() + " не найден");
        }
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Добавление лайка фильму {} от пользователя {}", filmId, userId);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Add like");
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Удаление лайка у фильма {} от пользователя {}", filmId, userId);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Remove like");
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getTopFilmsByLikes(int limit) {
        log.debug("Получение топ-{} фильмов по лайкам", limit);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Get top films by likes");
        
        List<Film> films = jdbcTemplate.query(sql, this::mapFilmWithMpa, limit);

        films.forEach(film -> {
            initializeFilmCollections(film);
            loadFilmGenres(film);
        });
        
        return films;
    }

    public List<Film> getLikedFilmsByUser(Long userId) {
        log.debug("Получение фильмов, лайкнутых пользователем {}", userId);
        
        String sql = sqlQueryLoader.getQuery("film-queries.sql", "Get liked films by user");
        
        List<Film> films = jdbcTemplate.query(sql, this::mapFilmWithMpa, userId);
        films.forEach(film -> {
            initializeFilmCollections(film);
            loadFilmGenres(film);
        });
        
        return films;
    }
}