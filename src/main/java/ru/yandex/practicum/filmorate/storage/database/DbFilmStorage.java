package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Genre;
import ru.yandex.practicum.filmorate.storage.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class DbFilmStorage extends BaseStorage<Film> implements FilmStorage {

    private static final String INSERT_FILMS_QUERY =
            "INSERT INTO films (name, description, \"releaseDate\", duration, rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_FILMS_QUERY =
            "UPDATE films SET name = ?, description = ?, \"releaseDate\" = ?, duration = ?, rating_id = ? " +
                    "WHERE film_id = ?";

    private static final String FIND_ALL_QUERY =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM films AS f " +
                    "LEFT JOIN ratings AS r ON f.rating_id = r.rating_id";

    private static final String FIND_BY_ID =
            "SELECT f.*, r.name AS mpa_name " +
                    "FROM films AS f " +
                    "LEFT JOIN ratings AS r ON f.rating_id = r.rating_id " +
                    "WHERE f.film_id = ?";

    public DbFilmStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                new Rating(rs.getInt("rating_id"), rs.getString("mpa_name"))
        );
    }

    private void saveGenres(Film film) {
        final String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbc.update(deleteSql, film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        List<Genre> genres = film.getGenres().stream()
                .filter(g -> g != null && g.getId() != null)
                .collect(java.util.stream.Collectors.toMap(
                        Genre::getId,
                        g -> g,
                        (a, b) -> a // если дубль — оставляем первый
                ))
                .values().stream()
                .sorted(java.util.Comparator.comparingInt(Genre::getId))
                .toList();

        final String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : genres) {
            getGenreById(genre.getId()); // валидация существования жанра
            jdbc.update(insertSql, film.getId(), genre.getId());
        }
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM genres AS g " +
                "JOIN film_genres AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";

        return new LinkedHashSet<>(jdbc.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), filmId));
    }

    private void loadGenresForFilms(List<Film> films) {
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        String sql = "SELECT g.genre_id, g.name, fg.film_id " +
                "FROM genres AS g " +
                "JOIN film_genres AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id IN (" +
                String.join(",", Collections.nCopies(films.size(), "?")) + ") " +
                "ORDER BY fg.film_id, g.genre_id";

        Object[] ids = films.stream().map(Film::getId).toArray();

        jdbc.query(sql, (rs) -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));

            if (filmMap.containsKey(filmId)) {
                filmMap.get(filmId).getGenres().add(genre);
            }
        }, ids);
    }

    @Override
    public List<Film> getPopular(int count) {

        String sql = "SELECT f.*, r.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN ratings AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id, r.name " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        List<Film> films = jdbc.query(sql, this::mapRowToFilm, count);

        loadGenresForFilms(films);

        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)";
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    @Override
    public Film getFilmFromStorage(Long filmId) {
        return findOne(FIND_BY_ID, filmId)
                .map(film -> {
                    film.setGenres(getGenresByFilmId(filmId));
                    return film;
                })
                .orElseThrow(() -> new NotFoundException("Фильм id=" + filmId + " не найден"));
    }

    @Override
    public Film addFilmInStorage(Film film) {

        Integer ratingId = (film.getMpa() != null) ? film.getMpa().getId() : null;

        getRatingById(ratingId);

        Long filmId = insert(INSERT_FILMS_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                ratingId
        );

        film.setId(filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film);
        }

        return film;
    }

    @Override
    public Film updateFilmInStorage(Film filmForUpdate) {

        int rowsUpdated = update(UPDATE_FILMS_QUERY,
                filmForUpdate.getName(),
                filmForUpdate.getDescription(),
                filmForUpdate.getReleaseDate(),
                filmForUpdate.getDuration(),
                filmForUpdate.getMpa() != null ? filmForUpdate.getMpa().getId() : null,
                filmForUpdate.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException("Фильм с id=" + filmForUpdate.getId() + " не найден");
        }

        Film filmInStorage = getFilmFromStorage(filmForUpdate.getId());

        if (filmInStorage != null) {
            return filmInStorage;
        } else {
            throw new NotFoundException("Ошибка при получении обновлённого фильма");
        }

    }

    @Override
    public void deleteFilmFromStorage(Long filmId) {

    }

    @Override
    public List<Film> getAllFilmsFromStorage() {
        List<Film> films = findMany(FIND_ALL_QUERY);

        if (films.isEmpty()) {
            return films;
        }

        loadGenresForFilms(films);

        return films;
    }

    @Override
    public List<Genre> getAllGenresFromStorage() {
        String sql = "SELECT * FROM genres";

        return jdbc.query(sql, genreRowMapper);
    }

    RowMapper<Genre> genreRowMapper = new RowMapper<Genre>() {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }
    };

    RowMapper<Rating> ratingRowMapper = new RowMapper<Rating>() {
        @Override
        public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
            Rating rating = new Rating();
            rating.setId(rs.getInt("rating_id"));
            rating.setName(rs.getString("name"));
            return rating;
        }
    };

    @Override
    public Genre getGenreById(Integer genreId) {
        String sql = "SELECT * FROM genres " +
                "WHERE genre_id = ?";
        try {
            return jdbc.queryForObject(sql, genreRowMapper, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id " + genreId + " не найден");
        }
    }

    @Override
    public List<Rating> getAllRatingsFromStorage() {
        String sql = "SELECT * FROM ratings";

        return jdbc.query(sql, ratingRowMapper);
    }

    @Override
    public Rating getRatingById(Integer ratingId) {
        String sql = "SELECT * FROM ratings " +
                "WHERE rating_id = ?";

        try {
            return jdbc.queryForObject(sql, ratingRowMapper, ratingId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг с id " + ratingId + " не найден");
        }
    }

    @Override
    public void validateFilmById(Long filmId) {
        if (filmId <= 0) {
            throw new ValidationException("id должен быть указан");
        }

        Optional<Film> filmWithOptional = findOne(FIND_BY_ID, filmId);
        if (!filmWithOptional.isPresent()) {
            throw new NotFoundException("Фильм с указанным номером (#" + filmId + ") отсутствует в хранилище");
        }
    }

}
