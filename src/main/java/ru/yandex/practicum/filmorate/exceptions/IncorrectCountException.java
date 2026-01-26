package ru.yandex.practicum.filmorate.exceptions;

public class IncorrectCountException extends RuntimeException {
    public IncorrectCountException(String message) {
        super(message);
    }
}
