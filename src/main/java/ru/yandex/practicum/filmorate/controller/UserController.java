package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Getter
@RequestMapping("/users")
public class UserController {

    private final UserValidator userValidator;
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {

        log.info("Получен HTTP-запрос на создание пользователя: {}", newUser);

        userValidator.validate(newUser);
        userService.addNewUserToStorage(newUser);

        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User userForUpdate) {
        log.info("Получен HTTP-запрос на обновление пользовательских данных: {}", userForUpdate);

        userValidator.validate(userForUpdate);
        userValidator.checkCorrectIdOrDropException(userForUpdate.getId());

        userService.getUserById(userForUpdate.getId());

        userService.updateUserInStorage(userForUpdate);
        return userForUpdate;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addNewUserFriend(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("Получен HTTP-запрос на обновление друзей пользователя: #{}, id друга для добавления: #{}", id, friendId);

        if (id == null || friendId == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (id <= 0 || friendId <= 0) {
            throw new IncorrectCountException("Номер пользователя должен начинаться от 1 и выше.");
        }

        User user = userService.getUserFromStorage(id);
        User friendUser = userService.getUserFromStorage(friendId);

        user.getFriends().add(friendUser.getId());
        friendUser.getFriends().add(user.getId());

        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteUserFromFriends(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("Получен HTTP-запрос на удаление пользователя (#{}) из списка друзей (user #{})", friendId, id);

        if (id == null || friendId == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (id <= 0 || friendId <= 0) {
            throw new IncorrectCountException("Номер пользователя должен начинаться от 1 и выше.");
        }


        User user = userService.getUserFromStorage(id);
        User friendUser = userService.getUserFromStorage(friendId);


        user.getFriends().remove(friendUser.getId());
        friendUser.getFriends().remove(user.getId());

        return user;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriendsForUser(@PathVariable Long id) {

        log.info("Получен HTTP-запрос на получение списка всех друзей пользователя: #{}", id);

        if (id == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (id <= 0) {
            throw new IncorrectCountException("Номер пользователя должен начинаться от 1 и выше.");
        }

        User userForSearch = userService.getUserById(id);

        return userForSearch.getFriends().stream()
                .map(userService::getUserById)
                .filter(Objects::nonNull)
                .toList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getAllCommonFriendsWithUser(@PathVariable Long id, @PathVariable Long otherId) {

        log.info("Получен HTTP-запрос на получение общих друзей с пользователем: #{}, от (user #{})", otherId, id);

        if (id == null || otherId == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (id <= 0 || otherId <= 0) {
            throw new IncorrectCountException("Номер пользователя должен начинаться от 1 и выше.");
        }

        User firstUser = userService.getUserFromStorage(id);

        User secondUser = userService.getUserFromStorage(otherId);

        List<Long> commonFriendIds = firstUser.getFriends().stream()
                .filter(friendId -> secondUser.getFriends().contains(friendId))
                .collect(Collectors.toList());

        return commonFriendIds.stream()
                .map(userService::getUserById)
                .collect(Collectors.toList());
    }


    @GetMapping
    public Collection<User> getAllUsers() {

        log.info("Получен HTTP-запрос на получение списка всех пользователей");

        return userService.getAllUsersFromStorage().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .toList();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        log.info("Получен HTTP-запрос на получение пользователя по его номеру: #{}", id);
        return userService.getUserById(id);
    }

}