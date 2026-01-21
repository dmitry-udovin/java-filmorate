package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@Getter
@RequestMapping("/films")
public class FilmController {

    private final FilmValidator filmValidator;
    private final FilmService filmService;
    private final UserService userService;
    // private final InMemoryFilmStorage filmStorage;

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film newFilm) {

        log.info("Получен HTTP-запрос на создание фильма: {}", newFilm);

        filmValidator.validate(newFilm);

        newFilm.setId(Film.getNextId());

        //filmStorage.getFilmsById().put(newFilm.getId(), newFilm);

        filmService.addNewFilmToStorage(newFilm.getId(), newFilm);

        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film filmForUpdate) {
        log.info("Получен HTTP-запрос на обновление фильма: {}", filmForUpdate);

        filmValidator.validate(filmForUpdate);

        if (filmForUpdate.getId() <= 0) {
            throw new ValidationException("id должен быть указан");
        }

        // Film oldFilm = filmStorage.getFilmsById().get(filmForUpdate.getId());

        Film oldFilm = filmService.getFilmFromStorage(filmForUpdate.getId());

        if (oldFilm == null) {
            throw new NotFoundException("Фильм с id=" + filmForUpdate.getId() + " не найден");
        }

        //filmStorage.getFilmsById().put(filmForUpdate.getId(), filmForUpdate);

        filmService.addNewFilmToStorage(filmForUpdate.getId(), filmForUpdate);

        return filmForUpdate;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film updateFilmLikesInformation(@PathVariable Integer filmId, @PathVariable Long userId) {

        log.info("Получен HTTP-запрос на обновление информации о лайках фильма: {}, от пользователя: {}", filmId, userId);

        filmService.validateFilmByID(filmId);
        userService.validateUserByID(userId);

        Film filmForLike = filmService.getFilmFromStorage(filmId);

        if (!filmForLike.getUsersWhoLiked().contains(userId)) {
            filmForLike.getUsersWhoLiked().add(userId);
        } else {
            throw new IncorrectCountException("Пользователь с номером " + userId + " уже поставил лайк указанному фильму.");
        }

        return filmForLike;
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLikeFromFilmByUser(@PathVariable Integer filmId, @PathVariable Long userId) {

        log.info("Получен HTTP-запрос на удаление лайка, фильм #: {}, id пользователя: #{}", filmId, userId);

        filmService.validateFilmByID(filmId);
        userService.validateUserByID(userId);

        Film film = filmService.getFilmFromStorage(filmId);

        if (film.getUsersWhoLiked().contains(userId)) {
            film.getUsersWhoLiked().remove(userId);
        } else {
            throw new NotFoundException("Пользователь с номером " + userId + " не отмечал фильм как понравившийся.");
        }

        return film;

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
    public Collection<Film> getFilms() {
//        return filmStorage.getFilmsById().values().stream()
//                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
//                .toList();

        log.info("Получен HTTP-запрос на получение всех фильмов");

        return filmService.getAllFilmsFromStorage().stream()
                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
                .toList();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        // Film film = filmStorage.getFilmsById().get(id);

        log.info("Получен HTTP-запрос на получение фильма по его номеру: #{}", id);

//        if (film == null) {
//            throw new NotFoundException("Фильм с номером " + id + " не найден.");
//        }

        return filmService.getFilmFromStorage(id);
    }

}
