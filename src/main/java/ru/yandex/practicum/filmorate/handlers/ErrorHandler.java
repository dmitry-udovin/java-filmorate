package ru.yandex.practicum.filmorate.handlers;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.EntityStorageException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectCountException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserRelationshipsException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    // Ошибки @Valid в теле запроса (NotBlank/Email/Positive/AssertTrue и т.п.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации тела запроса: {}", e.getMessage());
        return new ErrorResponse("Некорректные данные запроса", e.getMessage());
    }

    // Ошибки валидации параметров/путей
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e) {
        log.warn("Ошибка валидации параметров: {}", e.getMessage());
        return new ErrorResponse("Некорректные параметры запроса", e.getMessage());
    }

    // Невалидный JSON / неверный формат даты и т.п.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("Ошибка чтения запроса: {}", e.getMessage());
        return new ErrorResponse("Некорректный формат запроса", e.getMessage());
    }

    @ExceptionHandler({
            ValidationException.class,
            IncorrectCountException.class,
            UserRelationshipsException.class,
            EntityStorageException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException e) {
        log.warn("Bad Request: {}", e.getMessage());
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        log.warn("Not Found: {}", e.getMessage());
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServer(final InternalServerException e) {
        log.error("Непредвиденная ошибка сервера", e);
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable t) {
        log.error("Непредвиденная ошибка сервера", t);
        return new ErrorResponse("Внутренняя ошибка сервера", t.getMessage());
    }
}