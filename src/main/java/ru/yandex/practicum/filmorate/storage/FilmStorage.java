package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilmFromStorage(int filmId);

    void addFilmInStorage(Film film);

    void updateFilmInStorage(Film filmForUpdate);

    void deleteFilmFromStorage(Integer filmId);

    List<Film> getAllFilmsFromStorage();

}
