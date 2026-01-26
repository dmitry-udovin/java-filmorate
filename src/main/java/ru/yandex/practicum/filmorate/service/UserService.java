package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryUserStorage userStorage;

    public User getUserById(long id) {
        return userStorage.getUserFromStorage(id);
    }

    public User getUserFromStorage(Long userId) {
        return userStorage.getUserFromStorage(userId);
    }

    public void addNewUserToStorage(User user) {
        userStorage.addUserInStorage(user);
    }

    public void updateUserInStorage(User user) {
        userStorage.updateUserInStorage(user);
    }

    public List<User> getAllUsersFromStorage() {
        return userStorage.getAllUsersFromStorage();
    }

    public void checkUserExistsInStorage(Long userId) {
        userStorage.getUserFromStorage(userId);
    }

}
