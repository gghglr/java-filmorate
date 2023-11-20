package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Integer sentRequestId, Integer getRequestId) {
        if (sentRequestId > 0 && getRequestId > 0 && userStorage.getUserStorage().containsKey(sentRequestId) &&
                userStorage.getUserStorage().containsKey(getRequestId)) {
            User sentRequestUser = userStorage.getUserStorage().get(sentRequestId);
            User getRequestUser = userStorage.getUserStorage().get(getRequestId);
            sentRequestUser.addFriend(getRequestId);
            getRequestUser.addFriend(sentRequestId);
            return sentRequestUser;
        } else {
            throw new DataNotFoundException("Пользователь не найден");
        }
    }

    public User deleteFriend(Integer sentDelete, Integer getDelete) {
        if (userStorage.getUserStorage().containsKey(sentDelete) &&
                userStorage.getUserStorage().containsKey(getDelete)) {
            User sentDeleteUser = userStorage.getUserStorage().get(sentDelete);
            User getDeleteUser = userStorage.getUserStorage().get(getDelete);
            sentDeleteUser.deleteFriend(getDelete);
            getDeleteUser.deleteFriend(sentDelete);
            return sentDeleteUser;
        } else {
            throw new RuntimeException("Пользователь не найден");
        }
    }

    public List<User> showMutualFriend(Integer fistFriend, Integer secondFriend) {
        if (userStorage.getUserStorage().containsKey(fistFriend) &&
                userStorage.getUserStorage().containsKey(secondFriend)) {
            User firstUser = userStorage.getUserStorage().get(fistFriend);
            User secondUser = userStorage.getUserStorage().get(secondFriend);
            List<User> mutualFriends = new ArrayList<>();
            if (firstUser.getFriends() != null && secondUser.getFriends() != null) {
                firstUser.getFriends().stream().filter(fist -> secondUser.getFriends().contains(fist))
                        .forEach(fist -> mutualFriends.add(userStorage.getUserStorage().get(fist)));
                return mutualFriends;
            } else return new ArrayList<>();
        } else {
            throw new RuntimeException("Пользователь не найден");
        }
    }

    public List<User> showAllFriend(Integer id) {
        User user = userStorage.getUserStorage().get(id);
        List<User> allFriend = new ArrayList<>();
        user.getFriends().stream().forEach(friendId -> allFriend.add(userStorage.getUserStorage().get(friendId)));
        return allFriend;
    }

    public UserStorage getStorage() {
        return userStorage;
    }

    public User getById(Integer id) {
        if (userStorage.getUserStorage().containsKey(id)) {
            return userStorage.getUserStorage().get(id);
        } else {
            throw new DataNotFoundException("Пользователь не найден");
        }
    }
}


