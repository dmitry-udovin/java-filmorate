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
import ru.yandex.practicum.filmorate.exceptions.UserRelationshipsException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Getter
@RequestMapping("/users")
public class UserController {

//    private Map<Long, User> usersById = new HashMap<>();
//
//    private final UserValidator userValidator = new UserValidator();

    private final UserValidator userValidator;
    private final UserService userService;
    private final InMemoryUserStorage userStorage;

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {

        log.info("Получен HTTP-запрос на создание пользователя: {}", newUser);

        userValidator.validate(newUser);

        newUser.setId(User.getNextId());

        userStorage.getUsersById().put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User userForUpdate) {
        log.info("Получен HTTP-запрос на обновление пользовательских данных: {}", userForUpdate);

        userValidator.validate(userForUpdate);

        if (userForUpdate.getId() <= 0) {
            throw new ValidationException("id должен быть указан");
        }

        User oldUser = userStorage.getUsersById().get(userForUpdate.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id=" + userForUpdate.getId() + " не найден");
        }

        userStorage.getUsersById().put(userForUpdate.getId(), userForUpdate);

        return userForUpdate;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addNewUserFriend(@PathVariable Long id, @PathVariable Long friendId) {

        if (id == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (id <= 0) {
            throw new IncorrectCountException("id должен начинаться от 1 и выше.");
        }

        if (friendId == null) {
            throw new NotFoundException("Для добавления нового друга должно быть указано его id.");
        }

        if (friendId <= 0) {
            throw new IncorrectCountException("id друга должен начинаться от 1 и выше.");
        }

        User user = userStorage.getUsersById().get(id);
        User friendUser = userStorage.getUsersById().get(friendId);

        if (user == null) {
            throw new NotFoundException("Попытка добавить друга несуществующему пользователю, id = " + id);
        }

        if (friendUser == null) {
            throw new NotFoundException("Друг с указанным friendId " + friendId + " не найден.");
        }

        user.getFriends().add(friendUser.getId());
        friendUser.getFriends().add(user.getId());

        return user;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteUserFromFriends(@PathVariable Long id, @PathVariable Long friendId) {

        if (id == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (id <= 0) {
            throw new IncorrectCountException("id должен начинаться от 1 и выше.");
        }

        if (friendId == null) {
            throw new NotFoundException("Чтобы удалить друга должен быть указан его id.");
        }

        if (friendId <= 0) {
            throw new IncorrectCountException("id друга для удаления должен начинаться от 1 и выше.");
        }

        User user = userStorage.getUsersById().get(id);
        User friendUser = userStorage.getUsersById().get(friendId);

        if (user == null) {
            throw new NotFoundException("Попытка удалить друга у несуществующего пользователя, id = " + id);
        }

        if (friendUser == null) {
            throw new NotFoundException("Пользователь для удаления из друзей не найден: id = " + friendId);
        }

        if (!user.getFriends().contains(friendUser.getId())) {
            throw new UserRelationshipsException("Ошибка исполнения: пользователи не являются друзьями! (удаление не требуется)");
        }

        user.getFriends().remove(friendUser.getId());
        friendUser.getFriends().remove(user.getId());

        return user;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriendsForUser(@PathVariable Long id) {
        if (id == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (id <= 0) {
            throw new IncorrectCountException("id должен начинаться от 1 и выше.");
        }

        User userForSearch = userStorage.getUsersById().get(id);

        if (userForSearch == null) {
            throw new NotFoundException("Попытка обращения к несуществующему пользователю, id = " + id);
        }

        List<User> friendsList = userForSearch.getFriends().stream()
                .map(userService::getUserById)
                .filter(Objects::nonNull)
                .toList();

        if (friendsList.isEmpty()) {
            throw new NotFoundException("Пользователь с id #" + id + " не имеет друзей.");
        }

        return friendsList;

    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getAllCommonFriendsWithUser(@PathVariable Long id, @PathVariable Long otherId) {

        if (id == null) {
            throw new NotFoundException("Не указан id 1-го пользователя.");
        }

        if (otherId == null) {
            throw new NotFoundException("Не указан id 2-го пользователя.");
        }

        if (id <= 0 || otherId <= 0) {
            throw new IncorrectCountException("id должен начинаться от 1 и выше.");
        }

        User firstUser = userStorage.getUsersById().get(id);

        if (firstUser == null) {
            throw new NotFoundException("Попытка обращения к несуществующему пользователю, id = " + id);
        }

        User secondUser = userStorage.getUsersById().get(otherId);

        if (secondUser == null) {
            throw new NotFoundException("Попытка обращения к несуществующему пользователю, id = " + otherId);
        }

        List<Long> commonFriendIds = firstUser.getFriends().stream()
                .filter(friendId -> secondUser.getFriends().contains(friendId))
                .collect(Collectors.toList());


        return commonFriendIds.stream()
                .map(userService::getUserById)
                .collect(Collectors.toList());

    }


    @GetMapping
    public Collection<User> getAllUsers() {
        return userStorage.getUsersById().values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .toList();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

}