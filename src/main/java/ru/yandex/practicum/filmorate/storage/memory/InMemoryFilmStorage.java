package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Genre;
import ru.yandex.practicum.filmorate.storage.Rating;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmsById = new HashMap<>();
    private final Map<Integer, Genre> genresById = new HashMap<>();
    private final Map<Integer, Rating> ratingsById = new HashMap<>();
    private long counter = 1;

    @Override
    public Film getFilmFromStorage(Long filmId) {
        if (filmsById.containsKey(filmId)) {
            return filmsById.get(filmId);
        } else {
            throw new NotFoundException("Фильм с номером #" + filmId + " не найден");
        }
    }

    @Override
    public Film addFilmInStorage(Film film) {
        film.setId(getNextId());
        filmsById.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilmInStorage(Film filmForUpdate) {
        validateFilmById(filmForUpdate.getId());
        filmsById.put(filmForUpdate.getId(), filmForUpdate);
        return filmForUpdate;
    }

    @Override
    public void deleteFilmFromStorage(Long filmId) {
        validateFilmById(filmId);
        filmsById.remove(filmId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return filmsById.values().stream()
                .sorted((f1, f2) -> f2.getUsersWhoLiked().size() - f1.getUsersWhoLiked().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = filmsById.get(filmId);
        if (film != null) {
            film.getUsersWhoLiked().add(userId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = filmsById.get(filmId);
        if (film != null) {
            film.getUsersWhoLiked().remove(userId);
        }
    }

    @Override
    public void validateFilmById(Long filmId) {
        if (filmId == null || !filmsById.containsKey(filmId)) {
            throw new NotFoundException("Фильм с id #" + filmId + " не найден.");
        }
    }

    @Override
    public List<Film> getAllFilmsFromStorage() {
        return new ArrayList<>(filmsById.values());
    }

    @Override
    public List<Genre> getAllGenresFromStorage() {
        if (genresById.isEmpty()) {
            genresById.put(1, new Genre(1, "Комедия"));
            genresById.put(2, new Genre(2, "Драма"));
            genresById.put(3, new Genre(3, "Мультфильм"));
            genresById.put(4, new Genre(4, "Триллер"));
            genresById.put(5, new Genre(5, "Документальный"));
            genresById.put(6, new Genre(6, "Боевик"));
        }
        return new ArrayList<>(genresById.values());
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        if (genresById.containsKey(genreId)) {
            return genresById.get(genreId);
        } else {
            throw new NotFoundException("Жанр с id " + genreId + " не найден");
        }
    }

    @Override
    public List<Rating> getAllRatingsFromStorage() {
        if (ratingsById.isEmpty()) {
            ratingsById.put(1, new Rating(1, "G"));
            ratingsById.put(2, new Rating(2, "PG"));
            ratingsById.put(3, new Rating(3, "PG-13"));
            ratingsById.put(4, new Rating(4, "R"));
            ratingsById.put(5, new Rating(5, "NC-17"));
        }
        return new ArrayList<>(ratingsById.values());
    }

    @Override
    public Rating getRatingById(Integer ratingId) {
        if (ratingsById.containsKey(ratingId)) {
            return ratingsById.get(ratingId);
        } else {
            throw new NotFoundException("Рейтинг с id " + ratingId + " не найден");
        }
    }

    private long getNextId() {
        return counter++;
    }
}