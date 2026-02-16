package ru.yandex.practicum.filmorate.converter;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class FilmConverter {

    public static FilmDto modelToDto(Film film) {
        return new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getFilmGenres(), film.getMpa());
    }

    public static Film dtoToModel(FilmDto filmDto) {
        return new Film(filmDto.getId(), filmDto.getName(), filmDto.getDescription(), filmDto.getReleaseDate(),
                filmDto.getDuration(), filmDto.getMpa());
    }

}
