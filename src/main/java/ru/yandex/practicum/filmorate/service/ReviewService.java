package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dbstorage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewDbStorage reviewDbStorage;
    private final JdbcTemplate jdbcTemplate;
    private FeedService feedService;

    @Autowired
    public ReviewService(ReviewDbStorage reviewDbStorage, JdbcTemplate jdbcTemplate, FeedService feedService) {
        this.reviewDbStorage = reviewDbStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.feedService = feedService;
    }

    public Review create(Review review) {
        validate(review);
        Review review1 = reviewDbStorage.createReview(review);
        feedService.addReviewEvent(review.getUserId(), review1.getReviewId());
        return review1;
    }

    public Review update(Review review) {
        validate(review);
        Review review1 = reviewDbStorage.update(review);
        feedService.updateReviewEvent(review1.getUserId(), review1.getReviewId());
        return review1;
    }

    public Review getById(Integer id) {
        log.info("Запрос на получение в сервисе");
        return reviewDbStorage.getById(id);
    }

    public void putReaction(Integer id, Integer userId, Boolean reaction) {
        reviewDbStorage.putReaction(id, userId, reaction);
    }

    public void deleteReaction(Integer id, Integer userId) {
        reviewDbStorage.deleteReaction(id, userId);
    }

    public List<Review> getReviews(Integer filmId, Integer count) {
        return reviewDbStorage.getReviews(filmId, count);
    }

    public void deleteReview(Integer id) {
        Review review = reviewDbStorage.getById(id);
        reviewDbStorage.deleteReview(id);
        feedService.createDeleteReviewEvent(review.getUserId(), id);
    }

    void validate(Review review) {
        if (review.getContent().isBlank()) {
            log.info("Попытка оставить пустой отзыв");
            throw new ValidationException("Пустой отзыв");
        }
        if (review.getUserId() <= 0) {
            log.info("Не существующий пользователь");
            throw new NotFoundException("Не существующий пользователь");
        }
        if (review.getFilmId() <= 0) {
            log.info("Не существующий фильм");
            throw new NotFoundException("Не существующий фильм");
        }
        if (review.getIsPositive() == null) {
            log.info("Поле не заполнено");
            throw new ValidationException("Поле не заполнено");
        }
    }
}
