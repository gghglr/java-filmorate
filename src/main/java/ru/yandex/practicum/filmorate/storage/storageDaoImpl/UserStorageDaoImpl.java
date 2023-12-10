package ru.yandex.practicum.filmorate.storage.storageDaoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class UserStorageDaoImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<User> getAllUser() {
        String sql = "SELECT * FROM users";
        Collection<User> users = jdbcTemplate.query(sql, userRowMapper());
        log.info("Из БД получен список пользователей:" + users);
        return users;
    }

    public User getUserById(Integer id) {
        try {
            String sql = "SELECT * FROM users WHERE user_id = ?";
            User user = jdbcTemplate.queryForObject(sql, userRowMapper(), id);
            log.debug("Успешно получили пользователя из бд с id= " + id);
            return user;
        } catch (RuntimeException e) {
            log.debug("Пользователь с id " + id + " не найден");
            throw new DataNotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, String> values = Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday().toString()
        );
        user.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        log.debug("В БД добавлен пользователь: " + user);
        return user;
    }

    public User update(User user) {
        if (getUserById(user.getId()) == null) {
            log.warn("Не найден пользователь с id=" + user.getId());
            throw new DataNotFoundException("Не найден пользователь с ID=" + user.getId());
        }
        String sql = "UPDATE users SET " +
                "name = ?, " +
                "login = ?, " +
                "email = ?, " +
                "birthday = ? " +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sql, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        log.debug("Обновлены в бд данные пользователя с id " + user.getId());
        return user;
    }

    protected RowMapper<User> userRowMapper() {
        return new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getString("name"),
                        rs.getDate("birthday").toLocalDate()
                );
                return user;
            }
        };
    }
}
