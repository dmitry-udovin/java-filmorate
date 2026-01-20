package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@Getter
@RequestMapping("/films")
public class FilmController {

    //private Map<Integer, Film> filmsById = new HashMap<>();

    //private final FilmValidator filmValidator = new FilmValidator();

    private final FilmValidator filmValidator;
    private final InMemoryFilmStorage storage;

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film newFilm) {

        log.info("Получен HTTP-запрос на создание фильма: {}", newFilm);

        filmValidator.validate(newFilm);

        newFilm.setId(Film.getNextId());

        storage.getFilmsById().put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film filmForUpdate) {
        log.info("Получен HTTP-запрос на обновление фильма: {}", filmForUpdate);

        filmValidator.validate(filmForUpdate);

        if (filmForUpdate.getId() <= 0) {
            throw new ValidationException("id должен быть указан");
        }

        Film oldFilm = storage.getFilmsById().get(filmForUpdate.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильм с id=" + filmForUpdate.getId() + " не найден");
        }

        storage.getFilmsById().put(filmForUpdate.getId(), filmForUpdate);

        return filmForUpdate;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return storage.getFilmsById().values().stream()
                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
                .toList();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        Film film = storage.getFilmsById().get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с номером " + id + " не найден.");
        }
        return film;
    }

}
