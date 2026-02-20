package ru.yandex.practicum.filmorate.storage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class DbUserStorage extends BaseStorage<User> implements UserStorage {
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

    @Autowired
    public DbUserStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    private void loadFriendRelations(User user) {
        String sql = "SELECT * FROM user_friends WHERE user_id = ? OR friend_id = ?";
        jdbc.query(sql, (rs) -> {
            long requesterId = rs.getLong("user_id");
            long receiverId = rs.getLong("friend_id");
            User.FriendStatus status = User.FriendStatus.valueOf(rs.getString("status"));

            User.FriendRelation relation = new User.FriendRelation(
                    null,
                    status,
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("confirmed_at") != null ? rs.getTimestamp("confirmed_at").toLocalDateTime() : null
            );

            if (requesterId == user.getId()) {
                relation.setFriendId(receiverId);
                user.getOutgoingRequests().put(receiverId, relation);
                if (relation.isConfirmed()) user.getFriends().add(receiverId);
            } else {
                relation.setFriendId(requesterId);
                user.getIncomingRequests().put(requesterId, relation);
                if (relation.isConfirmed()) user.getFriends().add(requesterId);
            }
        }, user.getId(), user.getId());
    }

    @Override
    public Optional<User> getUserFromStorage(long userId) {
        Optional<User> userOpt = findOne(FIND_BY_ID_QUERY, userId);
        userOpt.ifPresent(this::loadFriendRelations);
        return userOpt;
    }

    @Override
    public User addUserInStorage(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        long userId = insert(INSERT_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(userId);
        return user;
    }

    @Override
    public void deleteUserFromStorage(Long userId) {

    }

    @Override
    public User updateUserInStorage(User userForUpdate) {
        update(UPDATE_QUERY, userForUpdate.getEmail(), userForUpdate.getLogin(), userForUpdate.getName(),
                userForUpdate.getBirthday(), userForUpdate.getId());

        return getUserFromStorage(userForUpdate.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден после обновления"));
    }

    @Override
    public List<User> getAllUsersFromStorage() {
        List<User> users = findMany(FIND_ALL_QUERY);
        users.forEach(this::loadFriendRelations);
        return users;
    }

    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO user_friends (user_id, friend_id, status, created_at) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, userId, friendId, User.FriendStatus.UNCONFIRMED.name(), LocalDateTime.now());
    }

    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM users AS u " +
                "JOIN user_friends AS f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        List<User> friends = jdbc.query(sql, mapper, userId);
        friends.forEach(this::loadFriendRelations); // подгружаем ИХ друзей
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT u.* FROM users AS u " +
                "JOIN user_friends AS f1 ON u.user_id = f1.friend_id " +
                "JOIN user_friends AS f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        List<User> commonFriends = jdbc.query(sql, mapper, userId, otherId);
        commonFriends.forEach(this::loadFriendRelations);
        return commonFriends;
    }

}