package ru.yandex.practicum.filmorate.Controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.UserStorageDaoImpl;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorageDaoImpl userStorage;

    @BeforeEach
    public void setUp(){
        userStorage = new UserStorageDaoImpl(jdbcTemplate);
    }

    @Test
    public void testFindUserById() {
        User newUser = new User(1, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.create(newUser);

        User savedUser = userStorage.getUserById(1);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testUserUpdate() {
        User user = new User(2, "user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User updateUser = new User(2, "user@email.ru", "IVAAAAN", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.create(user);

        User newUser = userStorage.update(updateUser);

        assertThat(newUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updateUser);
    }
}