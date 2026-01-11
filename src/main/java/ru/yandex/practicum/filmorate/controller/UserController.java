package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> usersById = new HashMap<>();

    private final UserValidator userValidator = new UserValidator();

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {

        log.info("Получен HTTP-запрос на создание пользователя: {}", newUser);

        userValidator.validate(newUser);

        newUser.setId(User.getNextId());

        usersById.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User userForUpdate) {
        log.info("Получен HTTP-запрос на обновление пользовательских данных: {}", userForUpdate);

        userValidator.validate(userForUpdate);

        if (userForUpdate.getId() <= 0) {
            throw new ValidationException("id должен быть указан");
        }

        User oldUser = usersById.get(userForUpdate.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id=" + userForUpdate.getId() + " не найден");
        }

        usersById.put(userForUpdate.getId(), userForUpdate);

        return userForUpdate;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return usersById.values().stream()
                .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
                .toList();
    }

}