package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Set<Long> friends = new HashSet<>();

    private long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;
    private String name;
    private LocalDate birthday;

    @AssertTrue(message = "дата рождения не может быть указана в будущем")
    @JsonIgnore
    public boolean isBirthdayValid() {
        return birthday != null && !birthday.isAfter(LocalDate.now());
    }

}
