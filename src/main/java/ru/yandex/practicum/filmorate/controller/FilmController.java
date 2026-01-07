package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Integer, Film> filmsById = new HashMap<>();
    private Map<String, Film> filmsByNames = new HashMap<>();

    private final FilmValidator filmValidator = new FilmValidator();


    @PostMapping
    public Film createNewFilm(@RequestBody Film newFilm) {

        log.info("Получен HTTP-запрос на создание фильма: {}", newFilm);

        filmValidator.validate(newFilm);

        if (filmsByNames.containsKey(newFilm.getName())) {
            throw new ValidationException("Данное название уже используется. Выберите другое");
        }

        newFilm.setId(Film.getNextId());

        filmsByNames.put(newFilm.getName(), newFilm);
        filmsById.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film filmForUpdate) {
        log.info("Получен HTTP-запрос на обновление фильма: {}", filmForUpdate);

        filmValidator.validate(filmForUpdate);

        if (filmForUpdate.getId() <= 0) {
            throw new ValidationException("id должен быть указан");
        }

        Film oldFilm = filmsById.get(filmForUpdate.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильм с id=" + filmForUpdate.getId() + " не найден");
        }

        if (!oldFilm.getName().equals(filmForUpdate.getName())
                && filmsByNames.containsKey(filmForUpdate.getName())) {
            throw new ValidationException("Данное название уже используется. Выберите другое");
        }

        filmsByNames.remove(oldFilm.getName());

        filmsById.put(filmForUpdate.getId(), filmForUpdate);
        filmsByNames.put(filmForUpdate.getName(), filmForUpdate);

        return filmForUpdate;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmsByNames.values().stream()
                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
                .toList();
    }

}
