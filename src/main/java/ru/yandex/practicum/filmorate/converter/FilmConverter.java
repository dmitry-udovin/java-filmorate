package ru.yandex.practicum.filmorate.converter;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashSet;

@Component
public class FilmConverter {

    public static FilmDto modelToDto(Film film) {
        FilmDto dto = new FilmDto(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getGenres(),   // временно
                film.getMpa()
        );
        dto.setGenres(film.getGenres());
        return dto;
    }

    public static Film dtoToModel(FilmDto filmDto) {
        Film film = new Film(
                filmDto.getId(),
                filmDto.getName(),
                filmDto.getDescription(),
                filmDto.getReleaseDate(),
                filmDto.getDuration(),
                filmDto.getMpa()
        );
        if (filmDto.getGenres() != null) {
            film.setGenres(new LinkedHashSet<>(filmDto.getGenres()));
        }
        return film;
    }

}
