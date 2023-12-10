package ru.yandex.practicum.filmorate.storage.storageDaoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
public class MpaStorageDao implements MpaStorage {

    JdbcTemplate jdbcTemplate;

    public MpaStorageDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM ratings";
        return jdbcTemplate.query(sql, mpaRowMapper());
    }

    @Override
    public Mpa mpaById(Integer mpaId) {
        try {
            String sql = "SELECT * FROM ratings Where rating_id = ?;";
            return jdbcTemplate.queryForObject(sql, mpaRowMapper(), mpaId);
        } catch (RuntimeException e) {
            log.debug("Не найден mpa " + mpaId);
            throw new DataNotFoundException("рейтинг не найден");
        }
    }

    protected RowMapper<Mpa> mpaRowMapper() {
        return new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mpa mpa = new Mpa(
                        rs.getInt("rating_id"),
                        rs.getString("rating_name")
                );
                return mpa;
            }
        };
    }
}
