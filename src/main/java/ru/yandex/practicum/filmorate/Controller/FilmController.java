package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Component
@RestController
public class FilmController {

    private FilmStorage inMemoryFilmStorage;
    private FilmService filmService;

    @Autowired
    public FilmController() {
        filmService = new FilmService();
        inMemoryFilmStorage = filmService.getFilmStorage();
    }

    @GetMapping("/films")
    public List<Film> showAllFilm() {
        return inMemoryFilmStorage.getAllFilm();
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Set<Integer> setLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.setLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Set<Integer> deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopular(@RequestParam(required = false) Integer count) {
        if (count == null) {
            return filmService.getPopular(10);
        }
        return filmService.getPopular(count);
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

}
