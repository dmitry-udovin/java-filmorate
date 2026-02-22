package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmFromStorage(Long filmId);

    Film addFilmInStorage(Film film);

    Film updateFilmInStorage(Film filmForUpdate);

    void deleteFilmFromStorage(Long filmId);

    List<Film> getPopular(int count);

    List<Film> getAllFilmsFromStorage();

    List<Genre> getAllGenresFromStorage();

    Genre getGenreById(Integer genreId);

    List<Rating> getAllRatingsFromStorage();

    Rating getRatingById(Integer ratingId);

    void validateFilmById(Long filmId);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

}
