package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+")
    private String login;

    private String name;
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    public UserDto() {

    }

    public UserDto(final long id, final String email, final String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    @AssertTrue(message = "дата рождения не может быть указана в будущем")
    @JsonIgnore
    public boolean isBirthdayValid() {
        return birthday != null && !birthday.isAfter(LocalDate.now());
    }

    @Data
    @AllArgsConstructor
    public static class FriendRelation {
        private Long friendId;
        private User.FriendStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime confirmedAt;

        public boolean isConfirmed() {
            return status == User.FriendStatus.CONFIRMED;
        }

    }

    public enum FriendStatus {
        UNCONFIRMED, // запрос на дружбу отправлен/получен
        CONFIRMED    // дружба подтверждена
    }

}
