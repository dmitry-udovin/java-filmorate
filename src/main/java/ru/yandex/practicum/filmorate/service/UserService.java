package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityStorageException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User getUserById(long id) {
        User user = userStorage.getUsersById().get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        return user;
    }

    public void validateUserByID(Long userId) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (userId <= 0) {
            throw new IncorrectCountException("Номер пользователя должен начинаться от 1 и выше.");
        }

        if (!userStorage.getUsersById().containsKey(userId)) {
            throw new NotFoundException("Пользователь с указанным айди (#" + userId + ") отсутствует в списке.");
        }
    }

    public User getUserFromStorage(Long userId) {
        if (userStorage.getUsersById().containsKey(userId)) {
            return userStorage.getUsersById().get(userId);
        } else {
            throw new NotFoundException("Пользователь с указанным айди (#" + userId + ") отсутствует в списке.");
        }
    }

    public void addNewUserToStorage(Long userId, User user) {
        if (!userStorage.getUsersById().containsKey(userId)) {
            userStorage.getUsersById().put(userId, user);
        } else {
            throw new EntityStorageException("Пользователь с указанным id (#" + userId + ") уже содержится в хранилище.");
        }
    }

    public void updateFilmInStorage(User user) {
        if (userStorage.getUsersById().containsKey(user.getId())) {
            userStorage.getUsersById().put(user.getId(), user); // перезаписываем
        } else {
            throw new NotFoundException("Не найден пользователь с id = " + user.getId() + " для обновления.");
        }
    }

    public List<User> getAllUsersFromStorage() {
        if (!userStorage.getUsersById().isEmpty()) {
            return userStorage.getUsersById().values().stream().toList();
        } else {
            throw new EntityStorageException("Хранилище пустое, невозможно получить из него пользователей.");
        }
    }

    // smth else?

}
