package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    public void validate(User user) {
        if (user == null) {
            log.error("Ошибка валидации User: тело запроса пустое");
            throw new ValidationException("тело запроса пустое");
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации User: email пустой/некорректный");
            throw new ValidationException("почта отсутствует или не содержит символа @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации User: login пустой/с пробелами");
            throw new ValidationException("логин пустой или содержит пробелы");
        }

        if (user.getBirthday() == null) {
            log.error("Ошибка валидации User: birthday не указан");
            throw new ValidationException("дата рождения не указана");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации User: birthday в будущем");
            throw new ValidationException("дата рождения указана в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("User.name пустой — подставляем login");
            user.setName(user.getLogin());
        }
    }

}
