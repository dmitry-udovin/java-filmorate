package ru.yandex.practicum.filmorate.converter;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

public class UserConverter {
    public static User dtoToModel(UserDto dto) {
        User user = new User();

        user.setId(dto.getId() != null ? dto.getId() : 0);
        user.setEmail(dto.getEmail());
        user.setLogin(dto.getLogin());
        user.setName(dto.getName());
        user.setBirthday(dto.getBirthday());
        return user;
    }

    public static UserDto modelToDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setBirthday(user.getBirthday());
        userDto.setFriends(user.getAllConfirmedFriends());

        return userDto;
    }
}