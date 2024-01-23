package ru.yandex.practicum.filmorate.dbstorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.FilmStorageDao;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmDbStorage implements FilmStorageDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sql = "select * from film as f " +
                "inner join mpa as m on f.mpa = m.id ";
        return jdbcTemplate.query(sql, FilmDbStorage::makeFilm);
    }

    @Override
    public Film findById(int id) {
        String sql = "select * from film as f " +
                "inner join mpa as m on f.mpa = m.id " +
                "where f.film_id = ?";
        return jdbcTemplate.queryForObject(sql, FilmDbStorage::makeFilm, id);
    }

    @Override
    public int create(Film film) {
        String sqlQuery = "insert into film (name, description, release_data, duration, mpa, rate) " +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, (int) film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            statement.setInt(6, film.getRate());
            return statement;
        }, keyHolder);
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            saveFilmGenre(keyHolder.getKey().intValue(), genres);
        }

        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = new ArrayList<>(film.getDirectors());
            saveFilmDirector(keyHolder.getKey().intValue(), directors);
        }
        return keyHolder.getKey().intValue();
    }

    @Override
    public int update(Film film) {
        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_data = ?, duration = ?, mpa = ? , rate = ?" +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId());
        String sqlDelete = "delete from film_genre where id_film = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            saveFilmGenre(film.getId(), genres);
        }

        String sqlDeleteFilmDirector = "delete from film_director where film_id = ?";
        jdbcTemplate.update(sqlDeleteFilmDirector, film.getId());
        if (!film.getDirectors().isEmpty()) {
            List<Director> directors = new ArrayList<>(film.getDirectors());
            saveFilmDirector(film.getId(), directors);
        }
        return film.getId();
    }

    static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa"));
        mpa.setName(rs.getString("name_rate"));
        film.setId(rs.getInt("film_id"));
        film.setMpa(mpa);
        film.setDuration(rs.getInt("duration"));
        film.setDescription(rs.getString("description"));
        film.setName(rs.getString("name"));
        film.setReleaseDate(rs.getDate("release_data").toLocalDate());
        film.setRate(rs.getInt("rate"));

        return film;
    }

    private void saveFilmGenre(int idFilm, List<Genre> genres) {
        jdbcTemplate.batchUpdate("insert into film_genre (id_film, genre_id) values (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, idFilm);
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    private void saveFilmDirector(int idFilm, List<Director> directors) {
        String sql = "INSERT INTO film_director (film_id, director_id) VALUES (?,?);";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, idFilm);
                ps.setInt(2, directors.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    @Override
    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        List<Film> sortFilms = new ArrayList<>();

        if ("likes".equals(sortBy)) {
            String sql = "select * from film as f " +
                    "inner join mpa as m on f.mpa = m.id " +
                    "inner join film_director as fd on f.film_id=fd.film_id " +
                    "where fd.director_id = ? " +
                    "order by rate desc;";
            sortFilms = jdbcTemplate.query(sql, FilmDbStorage::makeFilm, directorId);

        }

        if ("year".equals(sortBy)) {
            String sql = "select * from film as f " +
                    "inner join mpa as m on f.mpa = m.id " +
                    "inner join film_director as fd on f.film_id=fd.film_id " +
                    "where fd.director_id = ? " +
                    "order by release_data;";
            sortFilms = jdbcTemplate.query(sql, FilmDbStorage::makeFilm, directorId);
        }

        return sortFilms;
    }

    @Override
    public void deleteFilm(int filmId) {
        String sql = "DELETE FROM film WHERE film_id = ?;";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> search(String query, String by) {
        List<Film> films = new ArrayList<>();
        String newQuery = "%" + query.toLowerCase() + "%";

        if ("title".equals(by)) {
            String sql = "SELECT f.*, m.name_rate FROM film f " +
                    "JOIN mpa m ON f.mpa = m.id " +
                    "WHERE LOWER(f.name) LIKE ? " +
                    "ORDER BY f.rate DESC;";
            films = jdbcTemplate.query(sql, FilmDbStorage::makeFilm, newQuery);
        }

        if ("director".equals(by)) {
            String sql = "SELECT f.*, m.name_rate, d.name_director " +
                    "FROM film f " +
                    "JOIN mpa m ON f.mpa = m.id " +
                    "JOIN film_director fd ON fd.film_id=f.film_id " +
                    "JOIN director d ON d.id = fd.director_id " +
                    "WHERE LOWER(d.name_director) LIKE ? " +
                    "ORDER BY f.rate DESC;";
            films = jdbcTemplate.query(sql, FilmDbStorage::makeFilm, newQuery);
        }

        if ("director,title".equals(by) || "title,director".equals(by)) {
            String sql = "SELECT f.*, m.name_rate, d.name_director " +
                    "FROM film f JOIN mpa m ON f.MPA = m.ID " +
                    "LEFT JOIN film_director fd ON fd.film_id=f.film_id " +
                    "LEFT JOIN director d ON d.id = fd.director_id " +
                    "WHERE LOWER(d.name_director) LIKE ? " +
                    "OR LOWER(f.name) LIKE ? " +
                    "ORDER BY f.rate;";
            films = jdbcTemplate.query(sql, FilmDbStorage::makeFilm, newQuery, newQuery);
        }

        return films;
    }
}
