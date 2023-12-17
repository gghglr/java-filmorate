package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmLikesStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.inMemory.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.storageDaoImpl.FriendsStorageDao;

import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;
    private FilmService filmService;
    private FilmStorage filmStorage;
    private UserService userService;
    private UserStorage userStorage;

    private FriendsStorageDao friendsStorage;
    private FilmLikesStorage filmLikesStorage;

    @BeforeEach
    public void setUp() {

        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage, friendsStorage);
        filmService = new FilmService(filmStorage, userService, filmLikesStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    public void testFilmValidationException() {
        Film film = new Film(1, "", "NewFilm", LocalDate.of(2023, 9, 9),
                120, null);
        Film film2 = new Film(2, "Tom", "SuperLongDescriptionSuperLongDescriptionSuperLong" +
                "DescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescription" +
                "SuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescription" +
                "SuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescription" +
                "SuperLongDescriptionSuperLongDescriptionSuperLongDescription",
                LocalDate.of(2023, 9, 9),
                120, null);
        Film film3 = new Film(3, "asd", "NewFilm", LocalDate.of(1800, 9, 9),
                120, null);

        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film2));
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film3));
    }

    @Test
    public void testCorrectFilm() {
        Film film = new Film(1, "Tom", "NewFilm", LocalDate.of(2020, 9, 9),
                120, null);
        Film film2 = new Film(2, "Tom", "NewFilm", LocalDate.of(2020, 11, 9),
                120, null);

        Assertions.assertEquals(film, filmController.create(film));
        Assertions.assertEquals(film2, filmController.create(film2));
    }

}