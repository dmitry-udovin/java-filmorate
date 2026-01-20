package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Service
@RequiredArgsConstructor
@Getter
public class FilmService {

    private final FilmStorage storage;

}
