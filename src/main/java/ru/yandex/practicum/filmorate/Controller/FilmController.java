package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
public class FilmController {

    private final Map<Integer, Film> filmStorage = new HashMap<>();
    private Integer generateId = 1;

    @GetMapping("/films")
    public List<Film> showAllFilm() {
        return new ArrayList<>(filmStorage.values());
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) {
        if (film.getName().isEmpty() || film.getName().equals("") || film.getDescription().length() >= 200 ||
                film.getDescription().equals("") || film.getReleaseDate()
                .compareTo(LocalDate.of(1895, 12, 28)) < 0 || film.getDuration() < 0) {
            log.error("ошибка в заполненных данных");
            throw new ValidationException("Ошибка в заполненных данных");
        }
        film.setId(generateId++);
        filmStorage.put(film.getId(), film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        if (filmStorage.containsKey(film.getId())) {
            filmStorage.remove(film.getId());
            filmStorage.put(film.getId(), film);
        } else {
            System.out.println("нечего доавбить");
            throw new RuntimeException("нечего обновлять");
        }
        return film;
    }

}
