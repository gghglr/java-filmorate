package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.Exception.DataNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Film {

    //таблица жанров: индитификатор жанра(ключ), название жанра
    //таблица лайков: filId, Id user, который поставил лайк
    private Integer filmId;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Integer> likes = new HashSet<>();
    private String rating;

    public void setLike(Integer id) {
        likes.add(id);
    }

    public void deleteLike(Integer id) {
        if (id <= 0) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        likes.remove(id);
    }

}
