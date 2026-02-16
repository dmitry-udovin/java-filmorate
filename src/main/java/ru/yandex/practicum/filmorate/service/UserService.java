package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.converter.UserConverter;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Data
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("dbUserStorage") final UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(long id) {
        return userStorage.getUserFromStorage(id);
    }

    public User getUserFromStorage(Long userId) {
        return userStorage.getUserFromStorage(userId);
    }

    public UserDto addNewUserToStorage(UserDto userDto) {

       User userModel = UserConverter.dtoToModel(userDto);

       User createdUser = userStorage.addUserInStorage(userModel);

       userDto = UserConverter.modelToDto(createdUser);

       return userDto;
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
