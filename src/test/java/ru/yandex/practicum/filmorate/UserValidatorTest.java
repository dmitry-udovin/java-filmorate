package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

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
