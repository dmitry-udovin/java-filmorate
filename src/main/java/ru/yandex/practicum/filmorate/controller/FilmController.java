package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

//        if(newFilm.getName() == null || newFilm.getName().isBlank()) {
//            log.error("ошибка валидации: пустое название фильма");
//            throw new ValidationException("Название не может быть пустым");
//        }
//
//        if(newFilm.getDescription().length() > 200) {
//            log.error("ошибка валидации: длина описания превышает лимит");
//            throw new ValidationException("Максимальная длина описания — 200 символов");
//        }
//
//        if(newFilm.getReleaseDate().isBefore(LocalDate.of(1895,Month.DECEMBER,28))) {
//            log.error("ошибка валидации: ранняя дата выпуска (before 28-12-1895)");
//            throw new ValidationException("Фильм выпущен раньше " + 28 + " декабря " + 1895 + " года.");
//        }
//
//        if(newFilm.getDuration().isNegative()) {
//            log.error("ошибка валидации: указана отрицательная продолжительность");
//            throw new ValidationException("Продолжительность фильма должна быть положительной.");
//        }

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

        if (!filmsByNames.containsKey(filmForUpdate.getName())) {
            log.error("ошибка обновления: фильма с названием {} не существует", filmForUpdate.getName());
            throw new ValidationException("Фильма с названием " + filmForUpdate.getName() + " не существует.");
        } // а должен ли уже существовать фильм? (тз?)

//        if(filmForUpdate.getName() == null || filmForUpdate.getName().isBlank()) {
//            log.error("ошибка обновления: не указано название фильма");
//            throw new ValidationException("Должно быть указано название.");
//        }
//
//        if(filmForUpdate.getDescription().length() > 200) {
//            log.error("ошибка обновления: превышен лимит описания");
//            throw new ValidationException("Максимальная длина нового описания — 200 символов");
//        }
//
//        if(filmForUpdate.getReleaseDate().isBefore(LocalDate.of(1895,Month.DECEMBER,28))) {
//            log.error("ошибка обновления: ранняя дата выпуска (before 28-12-1895)");
//            throw new ValidationException("Ошибка при обновлении даты: не раньше чем " + 28 + " декабря " + 1895 + " года.");
//        }
//
//        if(filmForUpdate.getDuration().isNegative()) {
//            log.error("ошибка обновления: указана отрицательная продолжительность");
//            throw new ValidationException("Продолжительность обновляемого фильма должна быть положительной.");
//        }

        Film oldFilm = filmsByNames.get(filmForUpdate.getName());
        filmForUpdate.setId(oldFilm.getId());

        filmsById.put(filmForUpdate.getId(), filmForUpdate);
        filmsByNames.put(filmForUpdate.getName(), filmForUpdate);
        return filmForUpdate;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmsByNames.values();
    }

}
