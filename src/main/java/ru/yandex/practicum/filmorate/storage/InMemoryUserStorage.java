package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> usersById = new HashMap<>();
    private static long counter = 1;

    public User getUserFromStorage(long userId) {
        if (usersById.containsKey(userId)) {
            return usersById.get(userId);
        } else {
            throw new NotFoundException("Пользователь с указанным айди (#" + userId + ") отсутствует в списке.");
        }
    }

    public void validateUserByID(Long userId) {
        if (userId == null) {
            throw new NotFoundException("Не указан id пользователя.");
        }

        if (userId <= 0) {
            throw new IncorrectCountException("Номер пользователя должен начинаться от 1 и выше.");
        }

        if (!usersById.containsKey(userId)) {
            throw new NotFoundException("Пользователь с указанным айди (#" + userId + ") отсутствует в списке.");
        }
    }

    public void addUserInStorage(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        user.setId(getNextId());
        usersById.put(user.getId(), user);
    }

    public void deleteUserFromStorage(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("Номер фильма для удаления не задан.");
        }
        if (userId <= 0) {
            throw new IncorrectCountException("Номер фильма должен начинаться от 1 и выше.");
        }

        if (usersById.containsKey(userId)) {
            usersById.remove(userId);
        } else {
            throw new NotFoundException("Пользователь с номером #" + userId + " отсутствует в хранилище.");
        }

    }

    public void updateUserInStorage(User userForUpdate) {
        Long userId = userForUpdate.getId();

        if (userId == null) {
            throw new IllegalArgumentException("Номер фильма для удаления не задан.");
        }
        if (userId <= 0) {
            throw new IncorrectCountException("Номер фильма должен начинаться от 1 и выше.");
        }

        if (!usersById.containsKey(userId)) {
            throw new NotFoundException("Не найден пользователь с номером #" + userId + " для обновления.");
        }

        usersById.put(userId, userForUpdate);
    }

    public List<User> getAllUsersFromStorage() {
        return usersById.values().stream().toList();
    }

    public static long getNextId() {
        return counter++;
    }

}
