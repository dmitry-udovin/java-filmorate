package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private static int counter = 1;

    private int id;
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

    public static int getNextId() {
        return counter++;
    }
}
