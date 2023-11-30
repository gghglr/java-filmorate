package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> userStorage = new HashMap<>();
    private Integer generatedId = 1;

    public List<User> getAllUser() {
        return new ArrayList<>(userStorage.values());
    }

    public User addUser(User user) {
        if (user.getEmail().isEmpty() || user.getEmail().indexOf("@") == -1 || user.getLogin().isEmpty() ||
                user.getLogin().indexOf(" ") != -1 || user.getLogin().equals("")
                || LocalDate.now().compareTo(user.getBirthday()) <= 0) {
            log.error("ошибка в заполненных данных");
            throw new ValidationException("Ошибка в заполненных данных");
        }
        user.setUserId(generatedId++);
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        userStorage.put(user.getUserId(), user);
        return user;
    }

    public User update(User user) {
        if (userStorage.containsKey(user.getUserId())) {
            userStorage.remove(user.getUserId());
            userStorage.put(user.getUserId(), user);
        } else {
            System.out.println("нечего добавить");
            throw new RuntimeException("нечего обновлять");
        }
        return user;
    }

    public Map<Integer, User> getUserStorage() {
        return userStorage;
    }
}
