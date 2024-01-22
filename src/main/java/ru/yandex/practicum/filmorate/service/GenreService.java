package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dbstorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage, JdbcTemplate jdbcTemplate) {
        this.genreDbStorage = genreDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public Genre findById(int id) {
        validFound(id);
        return genreDbStorage.findById(id);
    }

    private void validFound(int idGenre) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where id = ?", idGenre);
        if (!userRows.next()) {
            throw new NotFoundException("id " + idGenre + " не найден");
        }
    }
}
