package ru.yandex.practicum.filmorate.dbstorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.UserStorageDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDbStorage implements UserStorageDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List findAll() {
        String sql = "select * from user_filmorate";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User findById(int id) {
        String sql = "select * from user_filmorate where user_id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeUser, id);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_filmorate")
                .usingGeneratedKeyColumns("user_id");
        int key = simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue();
        return findById(key);
    }

    @Override
    public User update(User user) {
        String sqlQuery = "update user_filmorate set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return findById(user.getId());
    }

    public Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setLogin(rs.getString("login"));
        user.setEmail(rs.getString("email"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        try {
            String sql = "DELETE FROM user_filmorate where user_id = ?;";
            jdbcTemplate.update(sql, userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Нечего удалять");
        }

    }
}
