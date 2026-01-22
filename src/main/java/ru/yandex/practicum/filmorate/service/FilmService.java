package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityStorageException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public void validateFilmByID(Integer filmId) {
        if (filmId == null) {
            throw new NotFoundException("Не указан номер фильма.");
        }

        if (filmId <= 0) {
            throw new IncorrectCountException("Номер фильма не может быть отрицательным.");
        }

        if (!filmStorage.getFilmsById().containsKey(filmId)) {
            throw new NotFoundException("Фильм с указанным номером (#" + filmId + ") отсутствует в списке.");
        }
    }

    public void addNewFilmToStorage(Integer filmId, Film film) {
        if (!filmStorage.getFilmsById().containsKey(filmId)) {
            filmStorage.getFilmsById().put(filmId, film);
        } else {
            throw new EntityStorageException("Фильм с указанным номером (#" + filmId + ") уже содержится в хранилище.");
        }
    }

    public Film getFilmFromStorage(Integer filmId) {
        if (filmStorage.getFilmsById().containsKey(filmId)) {
            return filmStorage.getFilmsById().get(filmId);
        } else {
            throw new NotFoundException("Фильм с указанным номером (#" + filmId + ") отсутствует в списке.");
        }
    }

    public void updateFilmInStorage(Film film) {
        if (filmStorage.getFilmsById().containsKey(film.getId())) {
            filmStorage.getFilmsById().put(film.getId(), film); // перезаписываем
        } else {
            throw new NotFoundException("Не найден фильм по номеру #" + film.getId() + " для обновления.");
        }
    }

    public List<Film> getAllFilmsFromStorage() {
//        if (!filmStorage.getFilmsById().isEmpty()) {
//            return filmStorage.getFilmsById().values().stream().toList();
//        } else {
//            throw new EntityStorageException("Хранилище пустое, невозможно получить из него фильмы.");
//        }
        return filmStorage.getFilmsById().values().stream().toList();
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
