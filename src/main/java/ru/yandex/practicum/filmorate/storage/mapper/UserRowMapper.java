package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();

        long userId = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");

        Timestamp birthday = rs.getTimestamp("birthday");

        user.setId(userId);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);

        if (birthday != null) {
            user.setBirthday(LocalDate.from(birthday.toLocalDateTime()));
        }

        return user;
    }
}
