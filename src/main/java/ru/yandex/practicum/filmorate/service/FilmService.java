package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dbstorage.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private static final int MAX_SYMBOLS = 200;
    private static final LocalDate RELEASE_DATA = LocalDate.of(1895, 12, 28);
    private final FilmDbStorage filmDbStorage;
    private final FilmLikesDbStorage filmLikesDbStorage;
    private final JdbcTemplate jdbcTemplate;
    private final FeedService feedService;
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage,
                       FilmLikesDbStorage filmLikesDbStorage,
                       JdbcTemplate jdbcTemplate,
                       FeedService feedService,
                       DirectorDbStorage directorDbStorage) {
        this.filmDbStorage = filmDbStorage;
        this.filmLikesDbStorage = filmLikesDbStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.feedService = feedService;
        this.directorDbStorage = directorDbStorage;
    }

    public List<Film> findAll() {
        return filmDbStorage.findAll().stream().map(this::makeGenreForFilm).collect(Collectors.toList());
    }

    public Film findById(int id) {
        validFound(id);
        return makeGenreForFilm(filmDbStorage.findById(id));
    }

    public Film create(Film film) {
        validate(film);
        int id = filmDbStorage.create(film);
        log.info("Фильм создан с id {}", id);
        return makeGenreForFilm(filmDbStorage.findById(id));
    }

    public Film update(Film film) {
        validFound(film.getId());
        validate(film);
        return makeGenreForFilm(filmDbStorage.findById(filmDbStorage.update(film)));
    }

    public List<Film> getPopularFilmsWithGenreAndYear(int count, int idGenre, int year) {
        if (idGenre != -1 && year != -1) {
            validFoundForGenre(idGenre);
            validFoundForYear(year);
            return filmLikesDbStorage.getPopularFilmsWithGenreAndYear(count, idGenre, year)
                    .stream()
                    .map(film -> addDirector(makeGenreForFilm(film))).collect(Collectors.toList());
        }
        if (idGenre == -1 && year != -1) {
            validFoundForYear(year);
            return filmLikesDbStorage.getPopularFilmsWithYear(count, year)
                    .stream()
                    .map(film -> addDirector(makeGenreForFilm(film)))
                    .collect(Collectors.toList());
        }
        if (idGenre != -1 && year == -1) {
            validFoundForGenre(idGenre);
            return filmLikesDbStorage.getPopularFilmsWithGenre(count, idGenre)
                    .stream()
                    .map(film -> addDirector(makeGenreForFilm(film)))
                    .collect(Collectors.toList());
        }
        return filmLikesDbStorage.getPopularFilms(count)
                .stream()
                .map(film -> addDirector(makeGenreForFilm(film)))
                .collect(Collectors.toList());
    }

    public void deleteLike(int idFilm, int idUser) {
        validFound(idFilm);
        checkUserExists(idUser);
        filmLikesDbStorage.deleteLike(idFilm, idUser);
        feedService.createDeleteLikesEvent(idUser, idFilm);
    }

    public void addLikes(int idFilm, int idUser) {
        validFound(idFilm);
        checkUserExists(idUser);
        filmLikesDbStorage.addLikes(idFilm, idUser);
        feedService.addLikesEvent(idUser, idFilm);
    }

    void validate(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            log.info("Пользователь неверно ввел имя фильма: {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().isBlank() || film.getDescription().length() > MAX_SYMBOLS) {
            log.info("Пользователь неверно ввел описание фильма {}, количество символов {}", film.getDescription(),
                    film.getDescription().length());
            throw new ValidationException("Описание не может быть пустым или превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(RELEASE_DATA)) {
            log.info("Пользователь неверно указал дату релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            log.info("Пользователь неверно ввел продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void validFound(int idFilm) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from film where film_id = ?", idFilm);
        if (!userRows.next()) {
            throw new NotFoundException("id " + idFilm + " не найден");
        }
    }

    private void checkUserExists(int idUser) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user_filmorate where user_id = ?", idUser);
        if (!userRows.next()) {
            throw new NotFoundException("id " + idUser + " не найден");
        }
    }

    private void validFoundForGenre(int idGenre) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where id = ?", idGenre);
        if (!userRows.next()) {
            throw new NotFoundException("id " + idGenre + " не найден");
        }
    }

    private void validFoundForYear(int year) {
        if (year > LocalDate.now().getYear()) {
            throw new NotFoundException("Фильм еще не вышел, проверьте правильность написания года");
        }
    }

    private Film makeGenreForFilm(Film film) {
        Set<Genre> genres = new TreeSet<>((genre1, genre2) -> {
            if (genre1.getId() < genre2.getId()) return -1;
            else return 1;
        });
        String sql = "select fg.genre_id, g.genre from film_genre as fg " +
                "inner join genre as g on g.id = fg.genre_id " +
                "where fg.id_film = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, film.getId());
        if (result != null) {
            for (Map<String, Object> map : result) {
                Genre savedGenre = new Genre();
                savedGenre.setId((Integer) map.get("genre_id"));
                savedGenre.setName((String) map.get("genre"));
                genres.add(savedGenre);
            }
        }
        film.setGenres(genres);

        return addDirector(film);
    }

    private Film addDirector(Film film) {
        List<Director> directors = new ArrayList<>();
        String sql = "SELECT fd.director_id, d.name_director FROM film_director AS fd " +
                "INNER JOIN director AS d ON d.id = fd.director_id " +
                "WHERE fd.film_id = ?;";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, film.getId());
        if (result != null) {
            for (Map<String, Object> map : result) {
                Director saveDirector = new Director();
                saveDirector.setId((Integer) map.get("director_id"));
                saveDirector.setName((String) map.get("name_director"));
                directors.add(saveDirector);
            }
        }
        film.setDirectors(directors);

        return film;
    }

    public void delete(int filmId) {
        filmDbStorage.deleteFilm(filmId);
    }

    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        directorDbStorage.findById(directorId);
        return filmDbStorage.getDirectorFilms(directorId, sortBy).stream()
                .map(this::makeGenreForFilm)
                .collect(Collectors.toList());
    }

    public List<Film> search(String query, String by) {
        return filmDbStorage.search(query, by).stream()
                .map(this::makeGenreForFilm)
                .collect(Collectors.toList());
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        List<Film> usersLikedFilms = filmLikesDbStorage.getLikedFilms(userId, friendId);
        return usersLikedFilms;
    }
}