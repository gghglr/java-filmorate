package ru.yandex.practicum.filmorate.dbfilm;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dbstorage.*;
import ru.yandex.practicum.filmorate.dbstorage.dao.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    Film film = new Film();
    Film filmPostman = new Film();
    Film filmPostmanUpdate = new Film();
    Film newPostmanFilm = new Film();
    Film updatePostman = new Film();
    Film updateFilm = new Film();
    Film filmWithRate = new Film();
    Film filmUpdateWithRate = new Film();
    Film filmWithManyGenre = new Film();
    User user = new User();

    @BeforeEach
    public void beforeEach() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        Mpa mpa2 = new Mpa();
        mpa.setId(2);
        Mpa mpa3 = new Mpa();
        mpa.setId(3);
        Set<Genre> genre11 = new TreeSet<>((genre1, genre2) -> {
            if (genre1.getId() < genre2.getId()) return -1;
            else return 1;
        });
        Genre genre12 = new Genre();
        genre12.setId(1);
        genre11.add(genre12);
        Set<Genre> genre13 = new TreeSet<>((genre1, genre2) -> {
            if (genre1.getId() < genre2.getId()) return -1;
            else return 1;
        });
        Genre genre14 = new Genre();
        genre14.setId(2);
        genre13.add(genre14);
        Set<Genre> genre = new TreeSet<>((genre1, genre2) -> {
            if (genre1.getId() < genre2.getId()) return -1;
            else return 1;
        });
        Genre genre1 = new Genre();
        Genre genre2 = new Genre();
        Genre genre3 = new Genre();
        genre1.setId(1);
        genre2.setId(2);
        genre3.setId(3);
        genre.add(genre1);
        genre.add(genre2);
        genre.add(genre3);
        filmPostman.setName("nisi eiusmod");
        filmPostman.setDescription("adipisicing");
        filmPostman.setReleaseDate(LocalDate.of(1967, 03, 25));
        filmPostman.setDuration(100);
        filmPostman.setMpa(mpa);
        filmPostmanUpdate.setId(1);
        filmPostmanUpdate.setName("Film update");
        filmPostmanUpdate.setDescription("adipisicing");
        filmPostmanUpdate.setReleaseDate(LocalDate.of(1989, 4, 17));
        filmPostmanUpdate.setDuration(190);
        filmPostmanUpdate.setMpa(mpa);
        filmPostmanUpdate.setRate(4);
        newPostmanFilm.setName("New film");
        newPostmanFilm.setDescription("New film about friends");
        newPostmanFilm.setReleaseDate(LocalDate.of(1999, 4, 30));
        newPostmanFilm.setDuration(120);
        newPostmanFilm.setMpa(mpa);
        newPostmanFilm.setRate(4);
        newPostmanFilm.setGenres(genre11);
        updatePostman.setId(1);
        updatePostman.setName("Film updated");
        updatePostman.setDescription("New film description");
        updatePostman.setReleaseDate(LocalDate.of(1989, 4, 17));
        updatePostman.setDuration(190);
        updatePostman.setMpa(mpa);
        updatePostman.setRate(4);
        updatePostman.setGenres(genre13);
        film.setName("Фильм");
        film.setDuration(100);
        film.setDescription("Хороший фильм");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(mpa);
        film.setGenres(genre);
        filmWithRate.setName("Фильм");
        filmWithRate.setDuration(100);
        filmWithRate.setDescription("Хороший фильм");
        filmWithRate.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithRate.setMpa(mpa);
        filmWithRate.setRate(3);
        filmUpdateWithRate.setId(1);
        filmUpdateWithRate.setName("Film");
        filmUpdateWithRate.setDuration(200);
        filmUpdateWithRate.setDescription("Хороший film");
        filmUpdateWithRate.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmUpdateWithRate.setRate(3);
        filmUpdateWithRate.setMpa(mpa);
        filmWithManyGenre.setId(1);
        filmWithManyGenre.setName("Film");
        filmWithManyGenre.setDuration(200);
        filmWithManyGenre.setDescription("Хороший film");
        filmWithManyGenre.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmWithManyGenre.setMpa(mpa);
        filmWithManyGenre.setGenres(genre);
        updateFilm.setId(1);
        updateFilm.setName("Film");
        updateFilm.setDuration(200);
        updateFilm.setDescription("Хороший film");
        updateFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        updateFilm.setMpa(mpa);
        updateFilm.setGenres(genre);
        user.setName("Ivan Petrov");
        user.setEmail("user@email.ru");
        user.setLogin("vanya123");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testFindFilmById() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(film);
        filmDbStorage.create(filmWithRate);
        System.out.println();
        System.out.println(filmDbStorage.findById(2));
        System.out.println();
        filmDbStorage.findById(1);
        assertEquals(filmDbStorage.findById(1).getName(), "Фильм");
    }

    @Test
    public void testFindAllFilms() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(film);
        filmDbStorage.create(film);
        filmDbStorage.create(film);
        List<Film> films = filmDbStorage.findAll();
        assertEquals(films.size(), 3);
        assertEquals(films.get(1).getName(), "Фильм");
    }

    @Test
    public void testUpdateFilm() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(film);
        filmDbStorage.update(filmUpdateWithRate);
        assertEquals(filmDbStorage.findById(1).getName(), "Film");
    }

    @Test
    public void testAddLikesAndDeleteLikesAndPopularsFilm() {
        UserStorageDao userStorageDao = new UserDbStorage(jdbcTemplate);
        FilmStorageDao filmStorageDao = new FilmDbStorage(jdbcTemplate);
        FilmLikesStorageDao filmLikesStorageDao = new FilmLikesDbStorage(jdbcTemplate);
        userStorageDao.create(user);
        userStorageDao.create(user);
        userStorageDao.create(user);
        filmStorageDao.create(film);
        filmStorageDao.create(film);
        filmStorageDao.create(film);
        filmLikesStorageDao.addLikes(1, 1);
        filmLikesStorageDao.addLikes(1, 2);
        filmLikesStorageDao.addLikes(1, 3);
        filmLikesStorageDao.addLikes(2, 1);
        filmLikesStorageDao.addLikes(2, 2);
        filmLikesStorageDao.addLikes(3, 3);
        filmLikesStorageDao.addLikes(3, 2);
        assertEquals(filmLikesStorageDao.getPopularFilms(1).get(0).getId(), 1);
        filmLikesStorageDao.deleteLike(1, 1);
        filmLikesStorageDao.deleteLike(1, 2);
        filmLikesStorageDao.deleteLike(2, 2);
        assertEquals(filmLikesStorageDao.getPopularFilms(1).get(0).getId(), 3);
    }

    @Test
    public void testDeleteLikes() {
        UserStorageDao userStorageDao = new UserDbStorage(jdbcTemplate);
        FilmStorageDao filmStorageDao = new FilmDbStorage(jdbcTemplate);
        FilmLikesStorageDao filmLikesStorageDao = new FilmLikesDbStorage(jdbcTemplate);
        userStorageDao.create(user);
        filmStorageDao.create(film);
        filmLikesStorageDao.addLikes(1, 1);
        filmLikesStorageDao.deleteLike(1, 1);
    }

    @Test
    public void checkPostmanTest() {
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(filmPostman);
        filmDbStorage.update(filmPostmanUpdate);
        filmDbStorage.create(newPostmanFilm);
        filmDbStorage.update(updatePostman);
        System.out.println();
        System.out.println(filmDbStorage.findAll());
        System.out.println();
    }
}
