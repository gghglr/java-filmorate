package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Data
@Builder
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

}
