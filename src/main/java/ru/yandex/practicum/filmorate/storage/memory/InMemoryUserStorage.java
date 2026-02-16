package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersById = new HashMap<>();
    private long counter = 1;

    @Override
    public User getUserFromStorage(long userId) {
        return Optional.ofNullable(usersById.get(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь с id #" + userId + " не найден."));
    }

    public void validateUserByID(Long userId) {
        if (userId == null || !usersById.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id #" + userId + " не найден.");
        }
    }

    @Override
    public User addUserInStorage(User user) {
        user.setId(getNextId());
        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUserFromStorage(Long userId) {
        validateUserByID(userId);
        usersById.remove(userId);
    }

    @Override
    public User updateUserInStorage(User userForUpdate) {
        validateUserByID(userForUpdate.getId());
        usersById.put(userForUpdate.getId(), userForUpdate);
        return userForUpdate;
    }

    @Override
    public List<User> getAllUsersFromStorage() {
        return new ArrayList<>(usersById.values());
    }

    private long getNextId() {
        return counter++;
    }
}