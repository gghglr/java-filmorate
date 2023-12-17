package ru.yandex.practicum.filmorate.storage.storageDaoImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikesStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmLikesStorageDao implements FilmLikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorageDaoImpl filmStorageDao;

    @Override
    public void addLikeByFilmId(Integer filmId, Integer userId) {
        String sql = "INSERT INTO likes VALUES(?, ?);";
        jdbcTemplate.update(sql, filmId, userId);

    }

    @Override
    public void deleteLikeByFilmId(Integer filmId, Integer userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> topFilms(Integer count) {
        String sql = "SELECT count(user_id) AS quantity, f.*, r.rating_name " +
                "FROM likes l " +
                "RIGHT JOIN films f ON f.film_id = l.film_id  " +
                "JOIN ratings r ON r.rating_id = f.rating_id " +
                "GROUP BY f.film_id, r.rating_id " +
                "ORDER BY quantity DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, filmStorageDao.filmRowMapper(), count);
    }
}
