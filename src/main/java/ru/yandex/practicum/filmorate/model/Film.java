package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
public class Film {

    private static final LocalDate CINEMA_BIRTHDAY =
            LocalDate.of(1895, 12, 28);

    private static int counter = 1;

    private int id;
    @NotBlank
    private String name;
    @Length(min = 5, max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;

    @AssertTrue(message = "дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        return releaseDate != null && !releaseDate.isBefore(CINEMA_BIRTHDAY);
    }

    public static int getNextId() {
        return counter++;
    }
}
