package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityStorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final InMemoryFilmStorage filmStorage;

    public void validateFilmByID(Integer filmId) {
        filmStorage.validateFilmById(filmId);
    }

    public void addNewFilmToStorage(Film film) {
        filmStorage.addFilmInStorage(film);
    }

    public Film getFilmFromStorage(Integer filmId) {
        return filmStorage.getFilmFromStorage(filmId);
    }

    public void updateFilmInStorage(Film film) {
        filmStorage.updateFilmInStorage(film);
    }

    public List<Film> getAllFilmsFromStorage() {
        return filmStorage.getAllFilmsFromStorage();
    }

    public List<Film> getMorePopularFilmsByLikes(int count) {

        List<Film> popularFilmsList = getAllFilmsFromStorage().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getUsersWhoLiked().size(), f1.getUsersWhoLiked().size()))
                .limit(count)
                .toList();

        if (popularFilmsList.isEmpty()) {
            throw new EntityStorageException("Хранилище пустое, невозможно получить из него фильмы.");
        }

        return popularFilmsList;
    }

}