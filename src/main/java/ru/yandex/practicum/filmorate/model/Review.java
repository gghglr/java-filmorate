package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {

    private Integer reviewId;
    private String content;
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private Integer useful;

    public Review(Integer reviewId, String content, Boolean isPositive, Integer userId, Integer filmId, Integer useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
