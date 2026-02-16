package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Optional<Film> getFilmFromStorage(Long filmId);

    Film addFilmInStorage(Film film);

    Film updateFilmInStorage(Film filmForUpdate);

    void deleteFilmFromStorage(Long filmId);

    List<Film> getPopular(int count);

    List<Film> getAllFilmsFromStorage();

    void validateFilmById(Long filmId);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

}
