package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public void validate(Film f) {
        if (f == null) {
            log.error("ошибка валидации: тело запроса пустое");
            throw new ValidationException("тело запроса пустое");
        }

        if (f.getName() == null || f.getName().isBlank()) {
            log.error("ошибка валидации: пустое название фильма");
            throw new ValidationException("Название не может быть пустым");
        }

        if (f.getDescription() == null) {
            log.error("ошибка валидации: описание не указано");
            throw new ValidationException("описание не указано");
        }

        if (f.getDescription().length() > 200) {
            log.error("ошибка валидации: длина описания превышает лимит");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (f.getReleaseDate() == null) {
            log.error("ошибка валидации: дата релиза не указана");
            throw new ValidationException("дата релиза не указана");
        }

        if (f.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.error("ошибка валидации: ранняя дата выпуска (before 28-12-1895)");
            throw new ValidationException("Фильм выпущен раньше 28 декабря 1895 года.");
        }

        if (f.getDuration() <= 0) {
            log.error("ошибка валидации: продолжительность должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}