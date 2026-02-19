package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.converter.FilmConverter;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;


@Slf4j
@RestController
@RequiredArgsConstructor
@Getter
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createNewFilm(@Valid @RequestBody FilmDto newFilm) {

        log.info("Получен HTTP-запрос на создание фильма: {}", newFilm);

        FilmDto filmDto = filmService.addNewFilmToStorage(newFilm);

        return filmDto;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film filmForUpdate) {
        log.info("Получен HTTP-запрос на обновление фильма: {}", filmForUpdate);

        Film updatedFilm = filmService.updateFilmInStorage(filmForUpdate);

        return updatedFilm;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Получен HTTP-запрос на добавление лайка: фильм {}, от пользователя: {}", filmId, userId);

        filmService.addLike(filmId, userId);

    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLikeFromFilmByUser(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Получен HTTP-запрос на удаление лайка, фильм #: {}, id пользователя: #{}", filmId, userId);

        filmService.validateFilmByID(filmId);
        userService.checkUserExistsInStorage(userId);

        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMorePopularFilmsByLikes(@RequestParam(defaultValue = "10") int count) {

        log.info("Получен HTTP-запрос на получение ТОП-{} понравившихся фильмов", count);

        if (count <= 0) {
            throw new IncorrectCountException("Параметр count должен начинаться от 1 и выше.");
        }

        return filmService.getMorePopularFilmsByLikes(count);
    }

    @GetMapping
    public Collection<FilmDto> getFilms() {

        log.info("Получен HTTP-запрос на получение всех фильмов");

        return filmService.getAllFilmsFromStorage().stream()
                .map(FilmConverter::modelToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        log.info("Получен HTTP-запрос на получение фильма по его номеру: #{}", id);

        return filmService.getFilmFromStorage(id);
    }

}
