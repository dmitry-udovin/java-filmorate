package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUserFromStorage(long userId);

    User addUserInStorage(User user);

    void deleteUserFromStorage(Long userId);

    User updateUserInStorage(User userForUpdate);

    List<User> getAllUsersFromStorage();

}
