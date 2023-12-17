package ru.yandex.practicum.filmorate.storage.storageDaoImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmStorageDaoImpl")
@RequiredArgsConstructor
public class FilmStorageDaoImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenresDbStorage filmGenresDbStorage;

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate().toString(),
                "duration", film.getDuration(),
                "rating_id", film.getMpa().getId()
        );
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        film.setId(id.intValue());
        if (film.getGenres().size() != 0) {
            filmGenresDbStorage.addGenres(film);
        }
        log.debug("Фильм добавлен: " + film);
        return film;
    }

    public Film getFilmById(Integer id) {
        try {
            String sql = "SELECT * FROM films f JOIN ratings r ON r.rating_id = f.rating_id WHERE film_id = ?;";
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper(), id);
            return film;
        } catch (RuntimeException e) {
            log.debug("Фильм не найден");
            throw new DataNotFoundException("Фильм не найден");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) == null) {
            log.warn("Фильм не найден");
            throw new DataNotFoundException("Фильм не найден");
        } else {
            String sql = "UPDATE films SET " +
                    "name = ?, " +
                    "description  = ?, " +
                    "release_date  = ?, " +
                    "duration  = ?, " +
                    "rating_id = ? " +
                    "WHERE film_id = ?;";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getMpa().getId(), film.getId());
            filmGenresDbStorage.updateGenres(film);
            log.debug("Фильм обновлен");
            return getFilmById(film.getId());
        }
    }

    @Override
    public List<Film> getAllFilm() {
        String sql = "SELECT * FROM films f JOIN ratings r ON r.rating_id = f.rating_id ORDER BY film_id;";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper());
        log.info("Из БД получен списк фильмов:" + films);
        return films;
    }

    protected RowMapper<Film> filmRowMapper() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {

                Film film = new Film(
                        rs.getInt("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new Mpa(rs.getInt("rating_id"), rs.getString("rating_name"))
                );
                film.getGenres().addAll(filmGenresDbStorage.getGenresByFilmId(film.getId()));
                return film;
            }
        };
    }
}
