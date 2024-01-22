package ru.yandex.practicum.filmorate.dbstorage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorageDao {

    Review createReview(Review review);

    Review update(Review review);

    Review getById(Integer id);

    void putReaction(Integer id, Integer userId, Boolean reaction);

    void deleteReaction(Integer id, Integer userId);

    void deleteReview(Integer id);

    List<Review> getReviews(Integer filmId, Integer count);

}
