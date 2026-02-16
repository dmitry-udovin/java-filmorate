package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmsById = new HashMap<>();
    private long counter = 1;

    @Override
    public Optional<Film> getFilmFromStorage(Long filmId) {
        return Optional.ofNullable(filmsById.get(filmId));
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

    private long getNextId() {
        return counter++;
    }
}