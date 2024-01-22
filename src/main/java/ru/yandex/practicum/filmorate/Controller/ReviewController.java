package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@Component
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review reviewCreate(@RequestBody Review review) {
        log.info("Получен запрос на создание нового отзыва");
        return reviewService.create(review);
    }

    @PutMapping
    public Review reviewUpdate(@RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва");
        return reviewService.update(review);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable("id") Integer id) {
        log.info("Получен запрос на получение отзыва по ID");
        return reviewService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Оставлен лайк отзыву");
        reviewService.putReaction(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Оставлен дизлайк отзыву");
        reviewService.putReaction(id, userId, false);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Integer id) {
        log.info("Удален отзыв");
        reviewService.deleteReview(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Удален лайк отзыву");
        reviewService.deleteReaction(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Удален дизлайк отзыву");
        reviewService.deleteReaction(id, userId);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Integer filmId, @RequestParam(required = false) Integer count) {
        log.info("получен запрос на получение отзывов к фильму");
        if (count == null) {
            count = 10;
        }
        return reviewService.getReviews(filmId, count);
    }
}
