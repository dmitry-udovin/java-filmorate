package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Timestamp releaseDate = rs.getTimestamp("releaseDate");
        Integer duration = rs.getInt("duration");

        film.setId(id);
        film.setName(name);
        film.setDescription(description);

        if (releaseDate != null) {
            film.setReleaseDate(LocalDate.from(releaseDate.toLocalDateTime()));
        }

        film.setDuration(duration);

        Integer ratingId = rs.getInt("rating_id");
        if (ratingId > 0) {
            film.setMpa(new Rating(ratingId, rs.getString("mpa_name")));
        }

        return film;
    }

}
