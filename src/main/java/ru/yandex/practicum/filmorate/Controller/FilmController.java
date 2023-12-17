package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RestController
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> showAllFilm() {
        return filmService.getAllFilm();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        validator(film);
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        validator(film);
        return filmService.update(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> topFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.topFilms(count);
    }

    private void validator(Film film) {
        if (film.getName().isBlank()) {
            throw new ValidationException("Ошибка, пустое поле name= " + film.getName());
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Ошибка, описание больше 200 символов: " + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Ошибка, дата релиза раньше 28.12.1895: " + film.getReleaseDate());
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Ошибка , отрицательная продолжительность= " + film.getDuration());
        }
    }
}
