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
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@Getter
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto newUser) {

        log.info("Получен HTTP-запрос на создание пользователя: {}", newUser);
        return userService.addNewUserToStorage(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User userForUpdate) {
        log.info("Получен HTTP-запрос на обновление пользовательских данных: {}", userForUpdate);
        return userService.updateUserInStorage(userForUpdate);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addNewUserFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление друга: #{} для пользователя #{}", friendId, id);

        userService.addFriend(id, friendId);
        return userService.getUserFromStorage(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteUserFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен HTTP-запрос на удаление пользователя (#{}) из списка друзей (user #{})", friendId, id);

        userService.deleteFriend(id, friendId);
        return userService.getUserFromStorage(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriendsForUser(@PathVariable Long id) {
        log.info("Получен HTTP-запрос на получение списка всех друзей пользователя: #{}", id);

        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getAllCommonFriendsWithUser(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен HTTP-запрос на получение общих друзей с пользователем: #{}, от (user #{})", otherId, id);

        return userService.getCommonFriends(id, otherId);
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
        return userService.getUserFromStorage(id);
    }

}