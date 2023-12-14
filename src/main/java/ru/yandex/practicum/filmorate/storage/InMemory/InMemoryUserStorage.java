package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public List<User> getAllUser() {
        log.info("Получены все пользователи");
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(id++);
        log.info("Добавлен новый пользователь " + user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Не найден пользователь " + id);
            throw new DataNotFoundException("Не найден пользователь " + id);
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Не найден пользователь " + user.getId());
            throw new DataNotFoundException("Не найден пользователь " + user.getId());
        }
        log.info("Обновленны данные пользователя ", user);
        users.put(user.getId(), user);
        return user;
    }
}