package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> filmsById = new HashMap<>();
    private static int counter = 1;

    public Film getFilmFromStorage(int filmId) {
        if (filmsById.containsKey(filmId)) {
            return filmsById.get(filmId);
        } else {
            throw new NotFoundException("Фильм с указанным номером (#" + filmId + ") отсутствует в списке.");
        }
    }

    public void addFilmInStorage(Film film) {

        if (film == null) {
            throw new IllegalArgumentException("Film cannot be null");
        }

        film.setId(getNextId());
        filmsById.put(film.getId(), film);
    }

    public void updateFilmInStorage(Film filmForUpdate) {
        Integer filmId = filmForUpdate.getId();
        if (filmId == null) {
            throw new IllegalArgumentException("Номер фильма для обновления не задан.");
        }
        if (filmId <= 0) {
            throw new IncorrectCountException("Номер фильма должен начинаться от 1 и выше.");
        }
        if (!filmsById.containsKey(filmId)) {
            throw new NotFoundException("Не найден фильм по номеру #" + filmId + " для обновления.");
        }

        filmsById.put(filmId, filmForUpdate);
    }

    public void deleteFilmFromStorage(Integer filmId) {
        if (filmId == null) {
            throw new IllegalArgumentException("Номер фильма для удаления не задан.");
        }
        if (filmId <= 0) {
            throw new IncorrectCountException("Номер фильма должен начинаться от 1 и выше.");
        }

        if (filmsById.containsKey(filmId)) {
            filmsById.remove(filmId);
        } else {
            throw new NotFoundException("Фильм с номером #" + filmId + " отсутствует в хранилище.");
        }

    }

    public void validateFilmById(Integer filmId) {
        if (filmId == null) {
            throw new NotFoundException("Не указан номер фильма.");
        }

        if (filmId <= 0) {
            throw new IncorrectCountException("Номер фильма не может быть отрицательным.");
        }

        if (!filmsById.containsKey(filmId)) {
            throw new NotFoundException("Фильм с указанным номером (#" + filmId + ") отсутствует в списке.");
        }

    }

    public List<Film> getAllFilmsFromStorage() {
        return filmsById.values().stream().toList();
    }

    public static int getNextId() {
        return counter++;
    }

}
