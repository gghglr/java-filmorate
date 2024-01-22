package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private Logger log = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private FeedService feedService;

    @Autowired
    public UserController(UserService userService, FeedService feedService) {
        this.userService = userService;
        this.feedService = feedService;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") int id) {
        log.info("Получен запрос на поиск пользователя с id {}", id);
        return userService.findById(id);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с id {}", user.getId());
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("Получен запрос на добавление в друзья от пользователя с id {} для пользователя с id {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("Получен запрос на уделаение из друзей от пользователя с id {} для пользователя с id {}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable("id") int id) {
        log.info("Получен запрос на получение списка всех друзей пользователя с id {}", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        log.info("Получен запрос на получение списка общих друзей для пользователей с id {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEvent> getFeedForUser(@PathVariable("id") int idUser) {
        userService.findById(idUser);
        log.info("Получен запрос на получение списка последних событий для пользователей с id {}", idUser);
        return feedService.getFeedForUser(idUser);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") int id) {
        log.info("Получен запрос на получение рекомендаций для пользователя с id {}", id);
        return userService.getRecommendations(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") int userId) {
        log.info("Получен запрос на удаление пользователя с id = " + userId);
        userService.delete(userId);
    }
}