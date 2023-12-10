package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    public Film create(Film film);

    public Film updateFilm(Film film);

    public Film getFilmById(Integer id);

    public Collection<Film> getAllFilm();

    public Map<Integer, Film> getFilmMap();

}
