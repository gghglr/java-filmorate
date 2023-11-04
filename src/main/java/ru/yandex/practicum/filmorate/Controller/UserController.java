package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.Exсeption;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
public class UserController {

    private final Map<Integer, User> userStorage = new HashMap<>();
    private Integer generatedId = 1;

    @GetMapping("/users")
    public List<User> showAllFilm() {
        return new ArrayList<>(userStorage.values());
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        if (user.getEmail().isEmpty() || user.getEmail().indexOf("@") == -1 || user.getLogin().isEmpty() ||
                user.getLogin().indexOf(" ") != -1 || LocalDate.now().compareTo(user.getBirthday()) <= 0) {
            log.error("ошибка в заполненных данных");
            throw new Exсeption("Ошибка в заполненных данных");
        }
        user.setId(generatedId++);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        if (userStorage.containsKey(user.getId())) {
            userStorage.remove(user.getId());
            userStorage.put(user.getId(), user);
        } else {
            System.out.println("нечего доавбить");
            throw new RuntimeException("нечего обновлять");
        }
        return user;
    }
}
