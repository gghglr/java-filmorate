package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private int rate = 0;
    private Set<Genre> genres = new TreeSet<>((genre1, genre2) -> {
        if (genre1.getId() < genre2.getId()) return -1;
        else return 1;
    });
    private Mpa mpa;
    private List<Director> directors = new ArrayList<>();
}
