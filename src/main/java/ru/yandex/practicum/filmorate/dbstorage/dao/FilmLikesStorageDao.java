package ru.yandex.practicum.filmorate.dbstorage.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmLikesStorageDao {
    List<Film> getPopularFilms(int count);

    List<Film> getLikedFilms(int idUser1, int idUser2);

    List<Film> getRecommendations(int idUser);

    List<Film> getPopularFilmsWithGenreAndYear(int count, int idGenre, int year);

    List<Film> getPopularFilmsWithGenre(int count, int idGenre);

    List<Film> getPopularFilmsWithYear(int count, int year);

    void deleteLike(int idFilm, int idUser);

    void addLikes(int idFilm, int idUser);

}
