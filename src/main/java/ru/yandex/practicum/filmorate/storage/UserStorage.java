package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    public List<User> getAllUser();

    public User addUser(User user);

    public User update(User user);

    public Map<Integer, User> getUserStorage();
}
