package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@Component
public class FilmValidator {

    public void validate(Film f) {
        if (f == null) {
            log.error("ошибка валидации: тело запроса пустое");
            throw new ValidationException("тело запроса пустое");
        }
    }
}