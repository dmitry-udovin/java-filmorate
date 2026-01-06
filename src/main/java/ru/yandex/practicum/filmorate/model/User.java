package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private static int counter = 1;

    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public static int getNextId() {
        return counter++;
    }
}
