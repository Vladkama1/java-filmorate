package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    ReviewDTO findById(Long id);

    ReviewDTO saveReview(ReviewDTO reviewDTO);

    ReviewDTO updateReview(ReviewDTO reviewDTO);

    void delete(Long id);

    void addRate(Long userId, Long reviewId, Boolean rate);

    void deleteRate(Long userId, Long reviewId);

    List<ReviewDTO> getPopularReview(Integer count, Long filmId);
}
