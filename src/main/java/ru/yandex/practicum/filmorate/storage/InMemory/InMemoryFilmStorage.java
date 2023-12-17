package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @Override
    public List<Film> getAllFilm() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            log.info("Не найден фильм" + filmId);
            throw new DataNotFoundException("Не найден фильм" + filmId);
        }
        return film;
    }

    @Override
    public Film create(Film film) {
        film.setId(id++);
        log.debug("Добавлен новый фильм " + film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Ошибка при обновлении фильма" + film.getId());
            throw new DataNotFoundException("Ошибка при обновлении фильма " + film.getId());
        }
        log.debug("Обновлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }


}