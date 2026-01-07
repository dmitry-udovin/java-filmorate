package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {

    private final FilmValidator validator = new FilmValidator();

    @Test
    void shouldAllowReleaseDateOnBoundary() {
        Film film = new Film();

        film.setName("film");
        film.setDescription("ok");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1);

        assertDoesNotThrow(() -> validator.validate(film));
    }

    @Test
    void shouldThrowWhenDescriptionTooLong() {
        Film film = new Film();

        film.setName("film");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(1);

        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(film));
        assertEquals("Максимальная длина описания — 200 символов", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDurationNegative() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("ok");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-1);

        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(film));
        assertEquals("Продолжительность фильма должна быть положительной.", ex.getMessage());
    }

}
