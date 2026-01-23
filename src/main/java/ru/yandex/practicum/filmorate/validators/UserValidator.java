package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Component
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

    public void checkCorrectIdOrDropException(Long userId) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (userId <= 0) {
            throw new IncorrectCountException("Номер пользователя должен начинаться от 1 и выше.");
        }
    }

}
