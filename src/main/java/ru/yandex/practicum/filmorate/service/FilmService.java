package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikesStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.FilmStorageDaoImpl;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {

    private FilmStorage filmStorage;
    private final UserService userService;
    private final FilmLikesStorage filmLikesStorage;

    @Autowired
    public FilmService(FilmStorageDaoImpl filmStorage, UserService userService, FilmLikesStorage filmLikesStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmLikesStorage = filmLikesStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Collection<Film> getAllFilm() {
        return filmStorage.getAllFilm();
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        filmLikesStorage.addLikeByFilmId(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        filmLikesStorage.deleteLikeByFilmId(filmId, userId);
    }

    public List<Film> topFilms(Integer count) {
        return filmLikesStorage.topFilms(count);
    }

}
