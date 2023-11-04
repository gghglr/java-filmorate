package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class User {

    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public void setName(String name) {
        this.name = name;
    }

}
