package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.FriendsStorageDao;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.UserStorageDaoImpl;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private UserStorage userStorage;
    private final FriendsStorageDao friendsStorage;

    @Autowired
    public UserService(UserStorageDaoImpl userStorageDao, FriendsStorageDao friendsStorage) {
        this.userStorage = userStorageDao;
        this.friendsStorage = friendsStorage;
    }

    public Collection<User> getAllUser() {
        return userStorage.getAllUser();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (getUserById(userId) == null || getUserById(friendId) == null) {
            throw new DataNotFoundException("Не найдены пользователи с userId=" + userId + " или friendId=" + friendId);
        }
        friendsStorage.addFriend(userId, friendId);
        log.debug("Пользователю с id=" + userId + " добавлен друг с id=" + friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        friendsStorage.deleteFriend(userId, friendId);
        log.debug("У пользователя с id=" + userId + " удален друг с id=" + friendId);
    }

    public Collection<User> userFriends(Integer userId) {
        log.debug("Запрошен список друзей пользовтеля с id=" + userId);
        return friendsStorage.getFriendsByUserId(userId);
    }

    public List<User> commonFriends(Integer userId, Integer otherId) {
        return friendsStorage.commonFriends(userId, otherId);
    }


}


