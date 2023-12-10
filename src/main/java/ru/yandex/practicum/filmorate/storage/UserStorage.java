package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    public Collection<User> getAllUser();

    public User create(User user);

    public User getUserById(Integer id);

    public User update(User user);

}
