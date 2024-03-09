package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository(value = "reviewDB")
@RequiredArgsConstructor
public class ReviewDAOImpl implements ReviewDAO {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review save(Review review) {
        String sqlQuery = "INSERT INTO reviews(content, is_positive, user_Id, film_Id) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId().intValue());
            stmt.setInt(4, review.getFilmId().intValue());
            return stmt;
        }, keyHolder);
        Long reviewId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        review.setReviewId(reviewId);
        return findById(reviewId).orElse(null);
    }

    @Override
    public Optional<Review> findById(Long id) {
        String sqlQuery = "SELECT " +
                "    reviews.id, " +
                "    reviews.content, " +
                "    reviews.is_positive, " +
                "    reviews.user_Id, " +
                "    reviews.film_Id, " +
                "    COALESCE(SUM(CASE WHEN reviews_rate.rate = true THEN 1 WHEN reviews_rate.rate = false THEN -1 ELSE 0 END), 0) AS useful " +
                "FROM " +
                "    reviews " +
                "LEFT JOIN " +
                "    reviews_rate ON reviews.id = reviews_rate.review_id " +
                "WHERE " +
                "    reviews.id = ? " +
                "GROUP BY " +
                "    reviews.id;";
        List<Review> reviews = jdbcTemplate.query(sqlQuery, this::mapRowToReview, id);
        return reviews.stream().findFirst();
    }

    @Override
    public Optional<Review> update(Review review) {
        String sqlQuery = "UPDATE reviews SET " +
                "content = ?,is_positive = ? " +
                "WHERE id = ?";
        int update = jdbcTemplate.update(
                sqlQuery, review.getContent(), review.getIsPositive(),
                review.getReviewId()
        );
        if (update == 0) {
            throw new NotFoundException("Отзыв не найден!", HttpStatus.NOT_FOUND);
        }
        return findById(review.getReviewId());
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "DELETE FROM reviews WHERE id = ?;";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public boolean addRate(Long userId, Long reviewId, Boolean rate) {
        String sqlQuery = "INSERT INTO reviews_rate (user_id, review_id, rate)" +
                "VALUES ( ?,?,? ) ";
        try {
            jdbcTemplate.update(sqlQuery, userId, reviewId, rate);
            return true;
        } catch (DuplicateKeyException exception) {
            return false;
        }
    }

    @Override
    public boolean deleteRate(Long userId, Long reviewId) {
        String sqlQuery = "DELETE FROM reviews_rate " +
                "WHERE user_id = ? AND review_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, reviewId) > 0;
    }

    @Override
    public List<Review> getPopularReview(Integer count) {
        String sqlQuery = "SELECT " +
                "    reviews.id, " +
                "    reviews.content, " +
                "    reviews.is_positive, " +
                "    reviews.user_Id, " +
                "    reviews.film_Id, " +
                "    COALESCE(SUM(CASE WHEN reviews_rate.rate = true THEN 1 WHEN reviews_rate.rate = false THEN -1 ELSE 0 END), 0) AS useful " +
                "FROM " +
                "    reviews " +
                "LEFT JOIN " +
                "    reviews_rate ON reviews.id = reviews_rate.review_id " +
                "GROUP BY " +
                "    reviews.id " +
                "ORDER BY useful DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    @Override
    public List<Review> getPopularReviewByFilm(Long filmId, Integer count) {
        String sqlQuery = "SELECT " +
                "    reviews.id, " +
                "    reviews.content, " +
                "    reviews.is_positive, " +
                "    reviews.user_Id, " +
                "    reviews.film_Id, " +
                "    COALESCE(SUM(CASE WHEN reviews_rate.rate = true THEN 1 WHEN reviews_rate.rate = false THEN -1 ELSE 0 END), 0) AS useful " +
                "FROM " +
                "    reviews " +
                "LEFT JOIN " +
                "    reviews_rate ON reviews.id = reviews_rate.review_id " +
                "WHERE " +
                "    reviews.film_Id = ? " +
                "GROUP BY " +
                "    reviews.id " +
                "ORDER BY useful DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
    }

    @Override
    public boolean isExistById(Long id) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM reviews WHERE id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_Id"))
                .filmId(resultSet.getLong("film_Id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
