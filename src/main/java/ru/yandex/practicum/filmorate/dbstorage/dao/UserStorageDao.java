package ru.yandex.practicum.filmorate.dbstorage.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorageDao {
    List<User> findAll();

    User findById(int id);

    User create(User entity);

    User update(User entity);

    void deleteUser(int userId);
}
