package ru.yandex.practicum.filmorate.Controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.FilmGenresDbStorage;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.FilmLikesStorageDao;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.FilmStorageDaoImpl;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.GenreDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;
    private FilmGenresDbStorage filmGenresDbStorage;
    private FilmStorageDaoImpl filmStorage;
    private FilmLikesStorageDao filmLikesStorageDao;

    @BeforeEach
    public void setUp() {
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
        filmGenresDbStorage = new FilmGenresDbStorage(jdbcTemplate, genreDbStorage);
        filmStorage = new FilmStorageDaoImpl(jdbcTemplate, filmGenresDbStorage);
        filmLikesStorageDao = new FilmLikesStorageDao(jdbcTemplate, filmStorage);
    }

    @Test
    public void testFindFilmById() {
        Film film = new Film(1, "Tom", "NewFilm", LocalDate.of(2020, 9, 9),
                120, new Mpa(1, "G"));
        filmStorage.create(film);

        Film savedFilm = filmStorage.getFilmById(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void testFilmUpdate() {
        Film film = new Film(1, "Tom", "NewFilm", LocalDate.of(2020, 9, 9),
                120, new Mpa(1, "G"));
        filmStorage.create(film);
        Film updateFilm = new Film(1, "Tom", "NEWDESCRIPTION", LocalDate.of(2020, 9, 9),
                120, new Mpa(1, "G"));
        Film newFilm = filmStorage.updateFilm(updateFilm);

        assertThat(newFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateFilm);
    }
}