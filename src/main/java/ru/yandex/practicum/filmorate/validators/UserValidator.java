package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {

    public void validate(User user) {
        if (user == null) {
            log.error("Ошибка валидации User: тело запроса пустое");
            throw new ValidationException("тело запроса пустое");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("User.name пустой — подставляем login");
            user.setName(user.getLogin());
        }
    }

}
