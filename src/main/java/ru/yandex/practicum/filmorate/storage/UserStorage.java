package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    Map<Long, User> getUsersById();

   // Collection<User> getAllCommonFriendsWithUser(Long id1, Long id2);

}
