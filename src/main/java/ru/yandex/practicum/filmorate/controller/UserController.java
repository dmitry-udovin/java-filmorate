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
import ru.yandex.practicum.filmorate.converter.UserConverter;
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
    public UserDto updateUser(@Valid @RequestBody UserDto dtoForUpdate) {
        log.info("Получен HTTP-запрос на обновление пользовательских данных: {}", dtoForUpdate);

        User fromDto = UserConverter.dtoToModel(dtoForUpdate);
        User updatedUser = userService.updateUserInStorage(fromDto);

        return UserConverter.modelToDto(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addNewUserFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Добавление друга: #{} для пользователя #{}", friendId, id);

        userService.addFriend(id, friendId);
        User modelFromStorage = userService.getUserFromStorage(id);

        return UserConverter.modelToDto(modelFromStorage);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public UserDto deleteUserFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен HTTP-запрос на удаление пользователя (#{}) из списка друзей (user #{})", friendId, id);

        userService.deleteFriend(id, friendId);
        User modelFromStorage = userService.getUserFromStorage(id);

        return UserConverter.modelToDto(modelFromStorage);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getAllFriendsForUser(@PathVariable Long id) {
        log.info("Получен HTTP-запрос на получение списка всех друзей пользователя: #{}", id);

        return userService.getFriends(id).stream()
                .map(UserConverter::modelToDto)
                .toList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getAllCommonFriendsWithUser(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен HTTP-запрос на получение общих друзей с пользователем: #{}, от (user #{})", otherId, id);

        return userService.getCommonFriends(id, otherId).stream()
                .map(UserConverter::modelToDto)
                .toList();
    }


    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Получен HTTP-запрос на получение списка всех пользователей");

        return userService.getAllUsersFromStorage().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .map(UserConverter::modelToDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("Получен HTTP-запрос на получение пользователя по его номеру: #{}", id);

        User modelFromStorage = userService.getUserFromStorage(id);

        return UserConverter.modelToDto(modelFromStorage);
    }

}