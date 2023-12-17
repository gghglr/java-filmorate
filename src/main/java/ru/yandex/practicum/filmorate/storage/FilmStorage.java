package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    public Film create(Film film);

    public Film updateFilm(Film film);

    public Film getFilmById(Integer id);

    public List<Film> getAllFilm();

}
