package ru.yandex.practicum.filmorate.storage.storageDaoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genres;";
        return jdbcTemplate.query(sql, genreRowMapper());
    }

    @Override
    public Genre genreById(Integer genreId) {
        try {
            String sql = "SELECT * FROM genres WHERE genre_id = ?;";
            return jdbcTemplate.queryForObject(sql, genreRowMapper(), genreId);
        } catch (RuntimeException e) {
            log.warn("Не найден жанр с ID=" + genreId);
            throw new DataNotFoundException("Не найден жанр с ID=" + genreId);
        }
    }

    protected RowMapper<Genre> genreRowMapper() {
        return new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre_name")
                );
            }
        };
    }
}