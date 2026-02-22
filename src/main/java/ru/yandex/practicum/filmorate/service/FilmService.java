package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.converter.FilmConverter;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Genre;
import ru.yandex.practicum.filmorate.storage.Rating;

import java.util.List;

@Service
@Slf4j
@Data
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public void validateFilmByID(Long filmId) {
        filmStorage.validateFilmById(filmId);
    }

    public FilmService(@Qualifier("dbFilmStorage") final FilmStorage filmStorage, final UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Начата процедура добавления лайка фильму {} от пользователя {}", filmId, userId);

        validateFilmByID(filmId);
        userService.checkUserExistsInStorage(userId);

        filmStorage.addLike(filmId, userId);
    }

    public FilmDto addNewFilmToStorage(FilmDto filmDto) {

        Film filmModel = FilmConverter.dtoToModel(filmDto);

        Film createdFilm = filmStorage.addFilmInStorage(filmModel);

        filmDto = FilmConverter.modelToDto(createdFilm);

        return filmDto;
    }

    public Film getFilmFromStorage(Long filmId) {
        return filmStorage.getFilmFromStorage(filmId);
    }

    public Film updateFilmInStorage(Film film) {
        validateFilmByID(film.getId());
        return filmStorage.updateFilmInStorage(film);
    }

    public List<Film> getAllFilmsFromStorage() {
        return filmStorage.getAllFilmsFromStorage();
    }

    public List<Genre> getGenres() {
        return filmStorage.getAllGenresFromStorage();
    }

    public Genre getGenreById(Integer genreId) {
        return filmStorage.getGenreById(genreId);
    }

    public List<Rating> getRatings() {
        return filmStorage.getAllRatingsFromStorage();
    }

    public Rating getRatingById(Integer ratingId) {
        return filmStorage.getRatingById(ratingId);
    }

    public List<Film> getMorePopularFilmsByLikes(int count) {

        List<Film> popularFilmsList = filmStorage.getPopular(count);

        return popularFilmsList;
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.deleteLike(filmId, userId);
    }


}