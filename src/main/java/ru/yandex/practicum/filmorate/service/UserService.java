package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dbstorage.FilmLikesDbStorage;
import ru.yandex.practicum.filmorate.dbstorage.UserDbStorage;
import ru.yandex.practicum.filmorate.dbstorage.UserRelationshipDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;
    private final UserRelationshipDbStorage userRelationshipDbStorage;
    private final FeedService feedService;
    private final FilmLikesDbStorage filmLikesDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate,
                       UserRelationshipDbStorage userRelationshipDbStorage,
                       FilmLikesDbStorage filmLikesDbStorage, FeedService feedService) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.filmLikesDbStorage = filmLikesDbStorage;
        this.userRelationshipDbStorage = userRelationshipDbStorage;
        this.feedService = feedService;
    }

    public List<User> findAll() {
        return userDbStorage.findAll();
    }

    public User findById(int id) {
        validFound(id);
        return userDbStorage.findById(id);
    }

    public User create(User user) {
        validate(user);
        return userDbStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        validFound(user.getId());
        return userDbStorage.update(user);
    }

    public void addFriend(int idUser1, int idUser2) {
        validFound(idUser1);
        validFound(idUser2);
        userRelationshipDbStorage.addFriend(idUser1, idUser2);
        feedService.addFriendsEvent(idUser1, idUser2);
    }

    public void deleteFriend(int idUser1, int idUser2) {
        validFound(idUser1);
        validFound(idUser2);
        userRelationshipDbStorage.deleteFriend(idUser1, idUser2);
        feedService.createDeleteFriendsEvent(idUser1, idUser2);
    }

    public List<User> getCommonFriends(int idUser1, int idUser2) {
        validFound(idUser1);
        validFound(idUser2);
        return userRelationshipDbStorage.getCommonFriends(idUser1, idUser2);
    }

    public List<User> getAllFriends(int idUser) {
        validFound(idUser);
        return userRelationshipDbStorage.getAllFriends(idUser);
    }

    public List<Film> getRecommendations(int idUser) {
        validFound(idUser);
        return filmLikesDbStorage.getRecommendations(idUser).stream().map(film -> {
            try {
                return makeGenreForFilm(film);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    void validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || user.getEmail() == null) {
            log.info("Пользователь неверно ввел почту: {}", user.getEmail());
            throw new ValidationException("Неверный формат почты или поле не заполнено");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ") || user.getLogin() == null) {
            log.info("Пользователь неверно ввел логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Пользователь неверно ввел дату рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validFound(int idUser) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from user_filmorate where user_id = ?", idUser);
        if (!userRows.next()) {
            throw new NotFoundException("id " + idUser + " не найден");
        }
    }

    public void delete(int userId) {
        userDbStorage.deleteUser(userId);
    }

    private Film makeGenreForFilm(Film film) throws SQLException {
        Set<Genre> genres = new TreeSet<>((genre1, genre2) -> {
            if (genre1.getId() < genre2.getId()) return -1;
            else return 1;
        });
        String sql = "select fg.genre_id, g.genre from film_genre as fg " +
                "inner join genre as g on g.id = fg.genre_id " +
                "where fg.id_film = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, film.getId());
        if (result != null) {
            for (Map<String, Object> map : result) {
                Genre savedGenre = new Genre();
                savedGenre.setId((Integer) map.get("genre_id"));
                savedGenre.setName((String) map.get("genre"));
                genres.add(savedGenre);
            }
        }
        film.setGenres(genres);
        return film;
    }
}