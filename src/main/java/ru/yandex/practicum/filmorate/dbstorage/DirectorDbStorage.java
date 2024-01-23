package ru.yandex.practicum.filmorate.dbstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.DirectorStorageDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DirectorDbStorage implements DirectorStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sql = "select * from director;";
        return jdbcTemplate.query(sql, makeDirector());
    }

    @Override
    public Director findById(int id) {
        try {
            String sql = "select * from director where id = ?";
            return jdbcTemplate.queryForObject(sql, makeDirector(), id);
        } catch (RuntimeException e) {
            log.warn("Не найден режиссер с ID=" + id);
            throw new NotFoundException("Не найден режиссер с ID=" + id);
        }
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("director")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = Map.of(
                "name_director", director.getName()
        );
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        director.setId(id.intValue());

        log.debug("В БД добавлен режиссер: " + director);
        return director;
    }

    @Override
    public Director update(Director director) {
        if (findById(director.getId()) == null) {
            log.warn("Не найден режиссер с ID=" + director.getId());
            throw new NotFoundException("Не найден режиссер с ID=" + director.getId());
        }

        String sql = "update director SET " +
                "name_director = ?" +
                "where id = ?;";

        jdbcTemplate.update(sql, director.getName(), director.getId());

        log.debug("В БД внесены изменения по режиссеру: " + director);
        return findById(director.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "delete from director where id = ?;";
        jdbcTemplate.update(sql, id);
        log.debug("В БД удален режиссер с ID=" + id);
    }

   private RowMapper<Director> makeDirector() {
        return new RowMapper<Director>() {
            @Override
            public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
                Director director = new Director();
                director.setId(rs.getInt("id"));
                director.setName(rs.getString("name_director"));
                return director;
            }
        };
    }
}
