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
    public Optional<User> getUserFromStorage(long userId) {
        return Optional.ofNullable(usersById.get(userId));
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
    public void addFriend(long userId, long friendId) {
        User user = usersById.get(userId);
        User friend = usersById.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
        } else {
            throw new NotFoundException("Один из пользователей не найден");
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = usersById.get(userId);
        User friend = usersById.get(friendId);

        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }
    }

    @Override
    public List<User> getFriends(long userId) {
        return List.of();
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return List.of();
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