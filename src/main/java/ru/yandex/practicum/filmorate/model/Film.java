package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.storage.Genre;
import ru.yandex.practicum.filmorate.storage.Rating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {

    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    private Rating mpa;
    private Set<Genre> genres = new LinkedHashSet<>();

    // @JsonIgnore
    private Set<Long> usersWhoLiked = new HashSet<>(); // id

    public Film() {

    }

    public Film(long id, String name, String description, LocalDate releaseDate,
                int duration, Rating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

}