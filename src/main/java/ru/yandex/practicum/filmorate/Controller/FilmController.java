package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private Logger log = LoggerFactory.getLogger(FilmController.class);
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") int id) {
        log.info("Получен запрос на поиск фильма по id {}", id);
        return filmService.findById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос на добавление фильма в базу данных");
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с id {}", film.getId());
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikes(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Получен запрос на добавление лайка для фильма с id {} от пользователя с id {}", id, userId);
        filmService.addLikes(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Получен запрос на удаление лайка для фильма с id {} от пользователя с id {}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsWithGenreAndYear(@RequestParam(name = "count", defaultValue = "10") int count,
                                                      @RequestParam(name = "genreId", defaultValue = "-1") int idGenre,
                                                      @RequestParam(name = "year", defaultValue = "-1") int year) {
        log.info("Получен запрос на получение списка популярных фильмов");
        return filmService.getPopularFilmsWithGenreAndYear(count, idGenre, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable("directorId") int directorId, @RequestParam String sortBy) {
        log.info("Получен запрос на получение списка фильмов режиссера с id=" + directorId + ", отсротированных по - "
                + sortBy);
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable("filmId") int filmId) {
        log.info("Получен запрос на удаление фильма с id = " + filmId);
        filmService.delete(filmId);
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        log.info("Получен запрос на поиск фильмов по - " + by + ", строка поиска - " + query);
        return filmService.search(query, by);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam("userId") int userId, @RequestParam("friendId") int friendId) {
        log.info("Получен запрос на получение списка общих с другом фильмов");
        return filmService.getCommonFilms(userId, friendId);
    }
}
