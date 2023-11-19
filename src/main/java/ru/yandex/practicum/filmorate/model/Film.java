package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Film {

    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Integer> likes = new HashSet<>();

    public void setLike(Integer id) {
        likes.add(id);
    }

    public void deleteLike(Integer id) {
        if (id <= 0) {
            throw new NotFoundException("Пользователь не найден");
        }
        likes.remove(id);
    }

}
