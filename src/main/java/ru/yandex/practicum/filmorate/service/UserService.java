package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.converter.UserConverter;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Data
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("dbUserStorage") final UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private void validateUsersById(Long... ids) {
        for (Long userId : ids) {
            if (userId == null || userId <= 0) {
                throw new IncorrectCountException("ID пользователя должен быть положительным числом.");
            }
            getUserFromStorage(userId);
        }
    }

    public User getUserFromStorage(Long userId) {
        Optional<User> userFromStorage = userStorage.getUserFromStorage(userId);
        if (userFromStorage.isPresent()) {
            return userFromStorage.get();
        } else {
            throw new NotFoundException("Пользователь с номером #" + userId + " не найден");
        }
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        validateUsersById(userId, otherId);

        return userStorage.getCommonFriends(userId, otherId);
    }

    public List<User> getFriends(long userId) {
        validateUsersById(userId);

        return userStorage.getFriends(userId);
    }

    public void addFriend(long userId, long friendId) {
        validateUsersById(userId, friendId);

        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        validateUsersById(userId, friendId);

        userStorage.deleteFriend(userId, friendId);
    }

    public UserDto addNewUserToStorage(UserDto userDto) {

        User userModel = UserConverter.dtoToModel(userDto);

        User createdUser = userStorage.addUserInStorage(userModel);

        userDto = UserConverter.modelToDto(createdUser);

        return userDto;
    }

    public User updateUserInStorage(User user) {
        validateUsersById(user.getId());
        return userStorage.updateUserInStorage(user);
    }

    public List<User> getAllUsersFromStorage() {
        return userStorage.getAllUsersFromStorage();
    }

    public void checkUserExistsInStorage(Long userId) {
        userStorage.getUserFromStorage(userId);
    }

}
