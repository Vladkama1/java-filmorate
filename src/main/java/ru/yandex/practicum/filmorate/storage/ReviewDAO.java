package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDAO {
    Review save(Review review);

    Optional<Review> findById(Long id);

    Optional<Review> update(Review review);

    boolean delete(Long id);

    boolean addRate(Long userId, Long reviewId, Boolean rate);

    boolean deleteRate(Long userId, Long reviewId);

    List<Review> getPopularReview(Integer count);

    List<Review> getPopularReviewByFilm(Long filmId, Integer count);

    boolean isExistById(Long id);
}
