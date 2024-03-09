package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.FilmDAO;
import ru.yandex.practicum.filmorate.storage.ReviewDAO;
import ru.yandex.practicum.filmorate.storage.UserDAO;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDAO reviewDAO;
    private final FilmDAO filmDAO;
    private final UserDAO userDAO;
    private final ReviewMapper mapper;

    @Autowired
    ReviewServiceImpl(@Qualifier(value = "reviewDB") ReviewDAO reviewDAO,
                      @Qualifier(value = "filmDB") FilmDAO filmDAO,
                      @Qualifier(value = "userDB") UserDAO userDAO,
                      ReviewMapper mapper) {
        this.reviewDAO = reviewDAO;
        this.mapper = mapper;
        this.filmDAO = filmDAO;
        this.userDAO = userDAO;
    }

    @Override
    public ReviewDTO findById(Long id) {
        return mapper.toDTO(reviewDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public ReviewDTO saveReview(ReviewDTO reviewDTO) {
        existUser(reviewDTO.getUserId());
        existFilm(reviewDTO.getFilmId());
        return mapper.toDTO(reviewDAO.save(mapper.toModel(reviewDTO)));
    }

    @Override
    public ReviewDTO updateReview(ReviewDTO reviewDTO) {
        existUser(reviewDTO.getUserId());
        existFilm(reviewDTO.getFilmId());
        return mapper.toDTO(reviewDAO.update(mapper.toModel(reviewDTO))
                .orElseThrow(() -> new NotFoundException("Отзыв не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void delete(Long id) {
        boolean deleted = reviewDAO.delete(id);
        if (!deleted) {
            throw new NotFoundException("Отзыв не найден", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void addRate(Long userId, Long reviewId, Boolean rate) {
        existUser(userId);
        existReview(reviewId);
        boolean added = reviewDAO.addRate(userId, reviewId, rate);
        if (!added) {
            throw new AlreadyExistsException("Пользователь уже оценил этот отзыв");
        }
    }

    @Override
    public void deleteRate(Long userId, Long reviewId) {
        existUser(userId);
        existReview(reviewId);
        reviewDAO.deleteRate(userId, reviewId);
    }

    @Override
    public List<ReviewDTO> getPopularReview(Integer count) {
        return mapper.toListDTO(reviewDAO.getPopularReview(count));
    }

    @Override
    public List<ReviewDTO> getPopularReviewByFilm(Long filmId, Integer count) {
        existFilm(filmId);
        return mapper.toListDTO(reviewDAO.getPopularReviewByFilm(filmId, count));
    }

    private void existUser(Long userId) {
        boolean isExistUser = userDAO.isExistById(userId);
        if (!isExistUser) {
            throw new NotFoundException("User not found by ID: " + userId, HttpStatus.NOT_FOUND);
        }
    }

    private void existFilm(Long filmId) {
        boolean isExistFilm = filmDAO.isExistById(filmId);
        if (!isExistFilm) {
            throw new NotFoundException("Film not found by ID: " + filmId, HttpStatus.NOT_FOUND);
        }
    }

    private void existReview(Long reviewId) {
        boolean isExistReview = reviewDAO.isExistById(reviewId);
        if (!isExistReview) {
            throw new NotFoundException("Review not found by ID: " + reviewId, HttpStatus.NOT_FOUND);
        }
    }
}
