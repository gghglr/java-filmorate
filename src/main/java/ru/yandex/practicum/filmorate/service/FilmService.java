package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private FilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Set<Integer> setLike(Integer idFilm, Integer userId) {
        if (inMemoryFilmStorage.getFilmMap().containsKey(idFilm)) {
            Film film = inMemoryFilmStorage.getFilmMap().get(idFilm);
            film.setLike(userId);
            return film.getLikes();
        } else {
            throw new RuntimeException("Нет запрашеваемого фильма или поста");
        }
    }

    public Set<Integer> deleteLike(Integer idFilm, Integer userId) {
        if (inMemoryFilmStorage.getFilmMap().containsKey(idFilm)) {
            Film film = inMemoryFilmStorage.getFilmMap().get(idFilm);
            film.deleteLike(userId);
            return film.getLikes();
        } else {
            throw new RuntimeException("Нет запрашеваемого фильма или поста");
        }
    }

    public List<Film> getPopular(Integer count) {
        return inMemoryFilmStorage.sort(count);
    }

    public FilmStorage getFilmStorage() {
        return inMemoryFilmStorage;
    }

    public Film getFilmById(Integer id) {
        if (inMemoryFilmStorage.getFilmMap().containsKey(id)) {
            return inMemoryFilmStorage.getFilmMap().get(id);
        } else {
            throw new DataNotFoundException("Фильм не найден");
        }
    }

}
