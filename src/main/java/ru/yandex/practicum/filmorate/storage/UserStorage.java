package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public List<User> getAllUser();

    public User create(User user);

    public User getUserById(Integer id);

    public User update(User user);

}
