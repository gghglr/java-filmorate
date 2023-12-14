package ru.yandex.practicum.filmorate.storage.storageDaoImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FriendsStorageDao implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorageDaoImpl userStorageDao;

    @Override
    public List<User> getFriendsByUserId(Integer userId) {
        String sql = "SELECT * from users WHERE user_id in( SELECT friend_id from friends where user_id = ?);";
        return jdbcTemplate.query(sql, userStorageDao.userRowMapper(), userId);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friends VALUES(?,?);";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> commonFriends(Integer userId, Integer otherId) {
        Set<User> users = new HashSet<>(getFriendsByUserId(userId));
        Set<User> anotherUsers = new HashSet<>(getFriendsByUserId(otherId));
        List<User> commonFriends = new ArrayList<>();
        for (User user : users) {
            if (anotherUsers.contains(user)) {
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }


}
