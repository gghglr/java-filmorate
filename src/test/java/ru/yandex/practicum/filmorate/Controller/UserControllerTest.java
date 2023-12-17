package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.FriendsStorageDao;

import java.time.LocalDate;

class UserControllerTest {

    private UserController userController;
    private UserService userService;
    private UserStorage userStorage;
    private FriendsStorageDao friendsStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage, friendsStorage);
        userController = new UserController(userService);
    }

    @Test
    public void testUserEmptyName() {
        User user = new User(1, "asd@yandex.ru", "asd",
                "", LocalDate.of(2000, 10, 10));
        userController.create(user);

        Assertions.assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void testUserValidationException() {
        User user = new User(1, "asdyandex.ru", "asd",
                "", LocalDate.of(2000, 10, 10));
        User user2 = new User(2, "asd@yandex.ru", "",
                "", LocalDate.of(2000, 10, 10));
        User user3 = new User(3, "asd@yandex.ru", "asd",
                "", LocalDate.of(2030, 10, 10));

        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user2));
        Assertions.assertThrows(ValidationException.class, () -> userController.create(user3));
    }

    @Test
    public void testCorrectUser() {
        User user = new User(1, "asd@yandex.ru", "asd",
                "Pavel", LocalDate.of(2000, 10, 10));
        User user2 = new User(2, "asdasd@yandex.ru", "asdasd",
                "Olga", LocalDate.of(2010, 11, 10));


        Assertions.assertEquals(user, userController.create(user));
        Assertions.assertEquals(user2, userController.create(user2));
    }

}