package ru.yandex.practicum.filmorate.Compare;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class CompareFilmPopular implements Comparator<Film> {
    @Override
    public int compare(Film f1, Film f2) {
        if (f1.getLikes().size() == 0) {
            return 1;
        } else if (f2.getLikes().size() == 0) {
            return -1;
        }
        return f1.getLikes().size() - f2.getLikes().size();
    }
}