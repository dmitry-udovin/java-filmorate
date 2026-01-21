package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> filmsById = new HashMap<>();

}
