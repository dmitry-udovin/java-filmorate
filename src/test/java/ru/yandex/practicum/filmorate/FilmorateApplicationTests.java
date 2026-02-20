package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.DbUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final DbUserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM users");
        // сбрасываем автоинкремент (H2)
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");

        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "test@example.com", "testLogin", "testname", LocalDate.of(1980, 1, 1)
        );
    }


    @Test
    void testFindUserById() {
        Optional<User> userOptional = userStorage.getUserFromStorage(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("login", "testLogin")
                );
    }


    @Test
    void testCreateAndFindUser() {
        User newUser = new User();
        newUser.setEmail("user@gmail.com");
        newUser.setLogin("User2");
        newUser.setName("User Name");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.addUserInStorage(newUser);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(userStorage.getUserFromStorage(createdUser.getId()))
                .hasValue(createdUser);
    }

    @Test
    void testUserNotFound() {
        Optional<User> userOptional = userStorage.getUserFromStorage(999L);

        assertThat(userOptional).isEmpty();
    }

    @Test
    void testUpdatedUser() {
        User userForUpdate = userStorage.getUserFromStorage(1).orElseThrow();
        userForUpdate.setEmail("new@mail.com");
        userForUpdate.setLogin("newLogin");
        userForUpdate.setName("Name");

        User updated = userStorage.updateUserInStorage(userForUpdate);

        assertThat(updated.getEmail()).isEqualTo("new@mail.com");
        assertThat(updated.getLogin()).isEqualTo("newLogin");

        User fromDatabase = userStorage.getUserFromStorage(1L).orElseThrow();
        assertThat(fromDatabase.getEmail()).isEqualTo("new@mail.com");
    }

    @Test
    void testGetAllUsers() {
        User newUser = new User();
        newUser.setEmail("user2@mail.com");
        newUser.setLogin("user2");
        newUser.setName("User 2");
        newUser.setBirthday(LocalDate.of(1995, 5, 5));
        userStorage.addUserInStorage(newUser);

        List<User> allUsers = userStorage.getAllUsersFromStorage();

        org.assertj.core.api.Assertions.assertThat(allUsers.size()).isEqualTo(2);

        List<String> logins = allUsers.stream()
                .map(User::getLogin)
                .collect(Collectors.toList());

        org.assertj.core.api.Assertions.assertThat(logins).contains("testLogin", "user2");
    }

    @Test
    void testAddAndGetFriends() {
        // создаём второго пользователя
        User friend = new User();
        friend.setEmail("friend@mail.com");
        friend.setLogin("friend");
        friend.setName("Friend");
        friend.setBirthday(LocalDate.of(1992, 2, 2));
        User friendSaved = userStorage.addUserInStorage(friend);

        // добавляем в друзья к user_id=1 через репозиторий
        userStorage.addFriend(1L, friendSaved.getId());

        var friends = userStorage.getFriends(1L);

        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.getFirst().getLogin()).isEqualTo("friend");
    }

    @Test
    void testCommonFriends() {
        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("user2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(1993, 3, 3));
        User user2Saved = userStorage.addUserInStorage(user2);

        User common = new User();
        common.setEmail("common@mail.com");
        common.setLogin("common");
        common.setName("Common");
        common.setBirthday(LocalDate.of(1994, 4, 4));
        User commonSaved = userStorage.addUserInStorage(common);

        userStorage.addFriend(1, commonSaved.getId());
        userStorage.addFriend(user2Saved.getId(), commonSaved.getId());

        List<User> commonFriends = userStorage.getCommonFriends(1, user2Saved.getId());

        assertThat(commonFriends.size())
                .isEqualTo(1);
        assertThat(commonFriends.getFirst().getLogin()).isEqualTo("common");

    }


}
