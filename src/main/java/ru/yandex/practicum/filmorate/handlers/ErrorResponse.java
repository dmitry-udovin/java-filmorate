package ru.yandex.practicum.filmorate.handlers;

public class ErrorResponse {
    private String error;
    private String description;

    public ErrorResponse(final String error, final String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

}
