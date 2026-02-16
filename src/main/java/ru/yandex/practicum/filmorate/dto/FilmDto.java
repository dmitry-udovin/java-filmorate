package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Genre;
import ru.yandex.practicum.filmorate.storage.Rating;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {

    private static final LocalDate CINEMA_BIRTHDAY =
            LocalDate.of(1895, 12, 28);


    private long id;
    @NotBlank
    private String name;
    @Length(min = 5, max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;

    private Rating mpa;
    private Set<Genre> filmGenres = new LinkedHashSet<>();

    // @JsonIgnore
    private Set<Long> usersWhoLiked = new HashSet<>(); // id

    public FilmDto(final long id, final String name, final String description, LocalDate releaseDate,
                   int duration, Set<Genre> filmGenres, Rating mpa) {
        // временный конструктор, добавить остальные поля через @AllArgs
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.filmGenres = filmGenres;
        this.mpa = mpa;
    }

    @AssertTrue(message = "дата релиза не может быть раньше 28 декабря 1895 года")
    public boolean isReleaseDateValid() {
        return releaseDate != null && !releaseDate.isBefore(CINEMA_BIRTHDAY);
    }

}
