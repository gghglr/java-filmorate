package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Compare.CompareFilmPopular;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Integer generateId = 1;
    private final Map<Integer, Film> filmStorage = new HashMap<>();

    public Film updateFilm(Film film) {
        if (filmStorage.containsKey(film.getFilmId())) {
            filmStorage.remove(film.getFilmId());
            filmStorage.put(film.getFilmId(), film);
        } else {
            System.out.println("нечего добавить");
            throw new RuntimeException("нечего обновлять");
        }
        return film;
    }

    public Film addFilm(Film film) {
        if (film.getName().isEmpty() || film.getName().equals("") || film.getDescription().length() >= 200 ||
                film.getDescription().equals("") || film.getReleaseDate()
                .compareTo(LocalDate.of(1895, 12, 28)) < 0 || film.getDuration() < 0) {
            log.error("ошибка в заполненных данных");
            throw new ValidationException("Ошибка в заполненных данных");
        }
        film.setFilmId(generateId++);
        filmStorage.put(film.getFilmId(), film);
        return film;
    }

    public List<Film> getAllFilm() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public Map<Integer, Film> getFilmMap() {
        return filmStorage;
    }

    public List<Film> sort(Integer count) {
        CompareFilmPopular compareFilmPopular = new CompareFilmPopular();
        Set<Film> sortedList = new TreeSet(compareFilmPopular);
        if (getAllFilm().size() == 1) {
            return new ArrayList<>(getAllFilm());
        }
        sortedList.addAll(getAllFilm());
        if (sortedList.size() < count) {
            count = sortedList.size();
        }
        return sortedList.stream().limit(count).collect(Collectors.toList());
    }
}
