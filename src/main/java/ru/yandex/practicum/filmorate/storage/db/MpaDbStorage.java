package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.MpaItem;
import ru.yandex.practicum.filmorate.storage.db.util.SqlQueryLoader;

import java.util.List;
import java.util.Optional;

@Repository("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SqlQueryLoader sqlQueryLoader;

    private final RowMapper<MpaItem> mapper = (rs, rn) -> new MpaItem(rs.getLong("id"), rs.getString("code"));

    public List<MpaItem> findAll() {
        String sql = sqlQueryLoader.getQuery("mpa-queries.sql", "Find all mpa");
        return jdbcTemplate.query(sql, mapper);
    }

    public Optional<MpaItem> findById(Long id) {
        String sql = sqlQueryLoader.getQuery("mpa-queries.sql", "Find mpa by id");
        List<MpaItem> rows = jdbcTemplate.query(sql, mapper, id);
        return rows.stream().findFirst();
    }
}


