package ru.yandex.practicum.filmorate.dbuser;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dbstorage.UserDbStorage;
import ru.yandex.practicum.filmorate.dbstorage.UserRelationshipDbStorage;
import ru.yandex.practicum.filmorate.dbstorage.dao.UserRelationshipStorageDao;
import ru.yandex.practicum.filmorate.dbstorage.dao.UserStorageDao;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    User user = new User();
    User userUpdate = new User();

    @BeforeEach
    public void beforeEach() {
        user.setName("Ivan Petrov");
        user.setEmail("user@email.ru");
        user.setLogin("vanya123");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userUpdate.setName("Семен");
        userUpdate.setId(1);
        userUpdate.setBirthday(LocalDate.of(1996, 10, 16));
        userUpdate.setEmail("yandex@yandex.ru");
        userUpdate.setLogin("sam111");
    }

    @Test
    public void testFindUserById() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(user);
        User savedUser = userStorage.findById(1);
        assertEquals(savedUser.getId(), 1);
    }

    @Test
    public void testFindAllUsers() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        List<User> users = new ArrayList<>();
        users.add(userStorage.create(user));
        users.add(userStorage.create(user));
        assertEquals(userStorage.findAll().size(), 2);
        assertEquals(userStorage.findAll().get(1), userStorage.findById(2));
    }

    @Test
    public void testUpdateUser() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.create(user);
        userStorage.update(userUpdate);
        User savedUser = userStorage.findById(1);
        assertEquals(savedUser.getName(), userUpdate.getName());
    }

    @Test
    public void testAddFriendAndFindAllFriendsAndDeleteFriends() {
        UserStorageDao userStorageDao = new UserDbStorage(jdbcTemplate);
        UserRelationshipStorageDao userRelationshipStorageDao = new UserRelationshipDbStorage(jdbcTemplate);
        userStorageDao.create(user);
        userStorageDao.create(user);
        userStorageDao.create(userUpdate);
        userRelationshipStorageDao.addFriend(1, 2);
        userRelationshipStorageDao.addFriend(1, 3);
        List<User> friends = userRelationshipStorageDao.getAllFriends(1);
        assertEquals(friends.size(), 2);
        userRelationshipStorageDao.deleteFriend(1, 2);
        List<User> friendsWithDelete = userRelationshipStorageDao.getAllFriends(1);
        assertEquals(friendsWithDelete.size(), 1);
    }

    @Test
    public void testGetCommonFriends() {
        UserStorageDao userStorageDao = new UserDbStorage(jdbcTemplate);
        UserRelationshipStorageDao userRelationshipStorageDao = new UserRelationshipDbStorage(jdbcTemplate);
        userStorageDao.create(user);
        userStorageDao.create(user);
        userStorageDao.create(userUpdate);
        userStorageDao.create(userUpdate);
        userRelationshipStorageDao.addFriend(1, 2);
        userRelationshipStorageDao.addFriend(1, 3);
        userRelationshipStorageDao.addFriend(1, 4);
        userRelationshipStorageDao.addFriend(2, 3);
        userRelationshipStorageDao.addFriend(2, 4);
        List<User> commonFriends = userRelationshipStorageDao.getCommonFriends(1, 2);
        assertEquals(commonFriends.size(), 2);
        assertEquals(commonFriends.get(0).getId(), 3);
        userRelationshipStorageDao.getAllFriends(1);
    }
}