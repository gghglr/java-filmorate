package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import ru.yandex.practicum.filmorate.Exception.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> showAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {
        validator(user);
        return userService.create(user);
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        validator(user);
        return userService.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> userFriends(@PathVariable("id") Integer userId) {
        return userService.userFriends(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer otherId) {
        return userService.commonFriends(userId, otherId);
    }

    private void validator(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().isEmpty()) {
            throw new ValidationException("Ошибка, пустой email= " + user.getEmail());
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Ошибка, email без '@' = "
                    + user.getEmail());
        }
        if (user.getLogin().isEmpty()) {
            throw new ValidationException("Ошибка, пустое поле login= "
                    + user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка, пробелы в поле login= "
                    + user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка, такая дата еще не наступила= "
                    + user.getBirthday());
        }
    }
}