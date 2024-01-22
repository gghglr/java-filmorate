package ru.yandex.practicum.filmorate.dbstorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.UserRelationshipStorageDao;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserRelationshipDbStorage implements UserRelationshipStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public UserRelationshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int idUser1, int idUser2) {
        String sql = "insert into relationship (id, id_friend) values (?, ?)";
        jdbcTemplate.update(sql, idUser1, idUser2);
    }

    @Override
    public void deleteFriend(int idUser1, int idUser2) {
        String sql = "delete from relationship where (id = ? and id_friend = ?)";
        jdbcTemplate.update(sql, idUser1, idUser2);
    }

    @Override
    public List<User> getCommonFriends(int idUser1, int idUser2) {
        String sql = "select * from user_filmorate where user_id " +
                "in (select id_friend from relationship where (id = ? and id_friend " +
                "in (select id_friend from relationship where id = ?)))";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idUser1, idUser2);
        List<User> friends = new ArrayList<>();
        for (Map map : results) {
            User user = new User();
            user.setId((Integer) map.get("user_id"));
            user.setEmail((String) map.get("email"));
            user.setLogin((String) map.get("login"));
            user.setBirthday(((Timestamp) map.get("birthday")).toLocalDateTime().toLocalDate());
            user.setName((String) map.get("name"));
            friends.add(user);
        }
        return friends;
    }

    @Override
    public List<User> getAllFriends(int idUser) {
        String sql = "select * from user_filmorate where user_id in (select id_friend from relationship where id = ?)";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, idUser);
        List<User> friends = new ArrayList<>();
        for (Map map : results) {
            User user = new User();
            user.setId((Integer) map.get("user_id"));
            user.setEmail((String) map.get("email"));
            user.setLogin((String) map.get("login"));
            user.setBirthday(((Timestamp) map.get("birthday")).toLocalDateTime().toLocalDate());
            user.setName((String) map.get("name"));
            friends.add(user);
        }
        return friends;
    }
}
