package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(inMemoryFilmStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    public void testFilmValidationException() {
      Film film = new Film(1, "", "NewFilm", LocalDate.of(2023, 9, 9),
              120L, null);
        Film film2 = new Film(2, "Tom", "SuperLongDescriptionSuperLongDescriptionSuperLong" +
                "DescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescription" +
                "SuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescription" +
                "SuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescriptionSuperLongDescription" +
                "SuperLongDescriptionSuperLongDescriptionSuperLongDescription",
                LocalDate.of(2023, 9, 9),
                120L, null);
        Film film3 = new Film(3, "asd", "NewFilm", LocalDate.of(1800, 9, 9),
                120L, null);

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film2));
        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film3));
    }

    @Test
    public void testCorrectFilm() {
        Film film = new Film(1, "Tom", "NewFilm", LocalDate.of(2020, 9, 9),
                120L, null);
        Film film2 = new Film(2, "Tom", "NewFilm", LocalDate.of(2020, 11, 9),
                120L, null);


        Assertions.assertEquals(film, filmController.addFilm(film));
        Assertions.assertEquals(film2, filmController.addFilm(film2));
    }

}