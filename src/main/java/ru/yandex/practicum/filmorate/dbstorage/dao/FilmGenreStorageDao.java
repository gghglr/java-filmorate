package ru.yandex.practicum.filmorate.dbstorage.dao;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmGenreStorageDao {
    void addFilm(Film film);

    void deleteFilm(int idFilm);
}
