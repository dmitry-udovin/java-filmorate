package ru.yandex.practicum.filmorate.exceptions;

public class EntityStorageException extends RuntimeException {
    public EntityStorageException(String message) {
        super(message);
    }
}
