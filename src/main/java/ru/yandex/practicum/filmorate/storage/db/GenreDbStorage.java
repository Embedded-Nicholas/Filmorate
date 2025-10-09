package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.GenreItem;
import ru.yandex.practicum.filmorate.storage.db.util.SqlQueryLoader;

import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SqlQueryLoader sqlQueryLoader;

    private final RowMapper<GenreItem> mapper = (rs, rn) -> new GenreItem(rs.getLong("id"), rs.getString("display_name"));

    public List<GenreItem> findAll() {
        String sql = sqlQueryLoader.getQuery("genre-queries.sql", "Find all genres");
        return jdbcTemplate.query(sql, mapper);
    }

    public Optional<GenreItem> findById(Long id) {
        String sql = sqlQueryLoader.getQuery("genre-queries.sql", "Find genre by id");
        List<GenreItem> rows = jdbcTemplate.query(sql, mapper, id);
        return rows.stream().findFirst();
    }
}


