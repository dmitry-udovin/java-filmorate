package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void shouldThrowWhenUserHasNullFields() {
        User user = new User();
        assertThrows(ValidationException.class, () -> validator.validate(user));
    }

    @Test
    void shouldThrowExpWhenEmailInvalid() {
        User user = new User();
        user.setEmail("not-that symbol");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(user));
        assertEquals("почта отсутствует или не содержит символа @", ex.getMessage());
    }

    @Test
    void shouldThrowExpWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("good.email@ru");
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(user));
        assertEquals("дата рождения указана в будущем", ex.getMessage());
    }

    @Test
    void shouldSetNameToLoginWhenNameBlank() {
        User user = new User();
        user.setEmail("good.email@ru");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(2000, 11, 4));

        validator.validate(user);
        assertEquals("Login", user.getName());

    }

}
