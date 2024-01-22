package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dbstorage.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage, JdbcTemplate jdbcTemplate) {
        this.mpaDbStorage = mpaDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public Mpa findById(int id) {
        validFound(id);
        return mpaDbStorage.findById(id);
    }

    private void validFound(int idMpa) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", idMpa);
        if (!userRows.next()) {
            throw new NotFoundException("id " + idMpa + " не найден");
        }
    }
}
