package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component
@RestController
public class UserController {

    private UserStorage inMemoryUserStorage;
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService =userService;
        inMemoryUserStorage = userService.getStorage();
    }

    @GetMapping("/users")
    public List<User> showAllUser() {
        return inMemoryUserStorage.getAllUser();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        return inMemoryUserStorage.addUser(user);
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        return inMemoryUserStorage.update(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> showAllFriend(@PathVariable int id) {
        return userService.showAllFriend(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> showMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.showMutualFriend(id, otherId);
    }
}
