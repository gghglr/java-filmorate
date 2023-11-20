package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUp() {
        InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        userController = new UserController(userService);
    }

    @Test
    public void testUserEmptyName() {
        User user = new User(1, "asd@yandex.ru", "asd",
                "", LocalDate.of(2000, 10, 10), null);
        userController.addUser(user);

        Assertions.assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void testUserValidationException() {
        User user = new User(1, "asdyandex.ru", "asd",
                "", LocalDate.of(2000, 10, 10), null);
        User user2 = new User(2, "asd@yandex.ru", "",
                "", LocalDate.of(2000, 10, 10), null);
        User user3 = new User(3, "asd@yandex.ru", "asd",
                "", LocalDate.of(2030, 10, 10), null);

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user2));
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user3));
    }

    @Test
    public void testCorrectUser() {
        User user = new User(1, "asd@yandex.ru", "asd",
                "Pavel", LocalDate.of(2000, 10, 10), null);
        User user2 = new User(2, "asdasd@yandex.ru", "asdasd",
                "Olga", LocalDate.of(2010, 11, 10), null);


        Assertions.assertEquals(user, userController.addUser(user));
        Assertions.assertEquals(user2, userController.addUser(user2));
    }

}