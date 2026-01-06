package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    public static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public void validate(Film film) {

        if (film == null) {
            log.error("Ошибка валидации Film: тело запроса пустое");
            throw new ValidationException("тело запроса пустое");
        }

        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации Film: name пустой");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() == null) {
            log.error("Ошибка валидации Film: description не указан");
            throw new ValidationException("описание не указано");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка валидации Film: description > 200");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.error("Ошибка валидации Film: releaseDate не указан");
            throw new ValidationException("дата релиза не указана");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.error("Ошибка валидации Film: releaseDate раньше 1895-12-28");
            throw new ValidationException("Фильм выпущен раньше 28 декабря 1895 года.");
        }

        Duration duration = film.getDuration();
        if (duration == null) {
            log.error("Ошибка валидации Film: duration не указан");
            throw new ValidationException("продолжительность не указана");
        }
        if (duration.isNegative()) {
            log.error("Ошибка валидации Film: duration отрицательный");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

}
