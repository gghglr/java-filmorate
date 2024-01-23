package ru.yandex.practicum.filmorate.dbstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.ReviewStorageDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorageDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {

        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Review createReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("review_id");
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        values.put("useful", 0);
        Number key = simpleJdbcInsert.executeAndReturnKey(values);
        review.setReviewId(key.intValue());
        log.info("Добавлен отзыв с ID = " + review.getReviewId());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "update review set content = ?, is_positive = ? where review_id = ?;";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        return getById(review.getReviewId());
    }

    @Override
    public Review getById(Integer id) {
        String sql = "select * from review where review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, reviewRowMapper(), id);
        } catch (RuntimeException e) {
            throw new NotFoundException("Отзыв не найден");
        }

    }

    @Override
    public void putReaction(Integer id, Integer userId, Boolean reaction) {
        String sql = "insert into review_likes values(?, ?, ?);";
        jdbcTemplate.update(sql, id, userId, reaction);
        Review review = getById(id);
        review.setUseful(useful(id));
    }

    public Integer useful(Integer id) {
        String sqlLikes = "select count(user_id)" +
                " from review_likes" +
                " where review_id = ? and is_positive = true";
        String sqlDislikes = "select count(user_id)" +
                " from review_likes" +
                " where review_id = ? and is_positive = false";
        Integer likeCount = jdbcTemplate.queryForObject(sqlLikes, Integer.class, id);
        Integer dislikeCount = jdbcTemplate.queryForObject(sqlDislikes, Integer.class, id);
        String sql = "update review set useful = ? where review_id = ?;";
        Integer useful = likeCount - dislikeCount;
        jdbcTemplate.update(sql, useful, id);
        return useful;
    }

    @Override
    public void deleteReaction(Integer id, Integer userId) {
        String sql = "delete from review_likes where review_id = ? and user_id = ?;";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteReview(Integer id) {
        String sql = "delete from review where review_id = ?;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        if (filmId == null) {
            String sql = "select * from review order by useful desc limit ?;";
            List<Review> reviews = jdbcTemplate.query(sql, reviewRowMapper(), count);
            return reviews;
        }
        String sql = "select * from review where film_id = ? order BY useful desc limit ?;";
        List<Review> reviews = jdbcTemplate.query(sql, reviewRowMapper(), filmId, count);
        return reviews;
    }

    protected RowMapper<Review> reviewRowMapper() {
        return new RowMapper<Review>() {
            @Override
            public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
                Review review = new Review(
                        rs.getInt("review_id"),
                        rs.getString("content"),
                        rs.getBoolean("is_positive"),
                        rs.getInt("user_id"),
                        rs.getInt("film_id"),
                        rs.getInt("useful")
                );
                return review;
            }
        };
    }
}
