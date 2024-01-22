package ru.yandex.practicum.filmorate.dbstorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.MpaStorageDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List findAll() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    @Override
    public Mpa findById(int id) {
        String sql = "select * from mpa where id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeMpa, id);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name_rate"));
        return mpa;
    }
}
