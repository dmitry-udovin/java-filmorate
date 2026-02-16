package ru.yandex.practicum.filmorate.storage.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Repository
public class DbUserStorage extends BaseStorage<User> implements UserStorage {
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    private void saveFriends(User user) {

        String deleteSql = "DELETE FROM user_friends WHERE user_id = ?";
        jdbc.update(deleteSql, user.getId());

        String insertSql = "INSERT INTO user_friends (user_id, friend_id, status, created_at, confirmed_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        for (User.FriendRelation relation : user.getOutgoingRequests().values()) {
            jdbc.update(insertSql,
                    user.getId(),
                    relation.getFriendId(),
                    relation.getStatus().name(),
                    relation.getCreatedAt(),
                    relation.getConfirmedAt()
            );
        }
    }

    @Override
    public User getUserFromStorage(long userId) {
        return null;
    }

    @Override
    public User addUserInStorage(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        long userId = insert(INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        user.setId(userId);

        // не нужно при регистрации

//        if (!user.getFriends().isEmpty()) {
//            saveFriends(user);
//        }

        return user;
    }

    @Override
    public void deleteUserFromStorage(Long userId) {

    }

    @Override
    public User updateUserInStorage(User userForUpdate) {
        return null;
    }

    @Override
    public List<User> getAllUsersFromStorage() {
        return List.of();
    }
}
