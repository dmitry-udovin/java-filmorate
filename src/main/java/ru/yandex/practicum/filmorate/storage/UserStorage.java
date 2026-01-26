package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUserFromStorage(long userId);

    void addUserInStorage(User user);

    void deleteUserFromStorage(Long userId);

    void updateUserInStorage(User userForUpdate);

    List<User> getAllUsersFromStorage();

}
