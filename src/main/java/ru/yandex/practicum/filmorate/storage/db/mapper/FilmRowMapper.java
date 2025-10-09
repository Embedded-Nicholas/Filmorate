package ru.yandex.practicum.filmorate.storage.db.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.MpaRating;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration_min"))
                .build();

        if (!rs.wasNull() && rs.getLong("mpa_id") != 0) {
            String mpaCode = rs.getString("mpa_code");
            film.setMpaRating(MpaRating.getByName(mpaCode));
        }
        
        return film;
    }
}
