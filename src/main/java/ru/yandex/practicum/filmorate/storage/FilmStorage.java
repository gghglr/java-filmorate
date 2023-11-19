package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public List<Film> getAllFilm();

    public Map<Integer, Film> getFilmMap();

    public List<Film> sort(Integer id);
}
