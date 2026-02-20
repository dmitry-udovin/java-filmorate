package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> getUserFromStorage(long userId);

    User addUserInStorage(User user);

    User updateUserInStorage(User user);

    List<User> getAllUsersFromStorage();

    void deleteUserFromStorage(Long userId);

    // Методы для работы с друзьями
    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);

}
