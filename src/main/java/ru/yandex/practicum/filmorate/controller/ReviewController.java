package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.anotation.MarkerOfCreate;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.yandex.practicum.filmorate.constants.ReviewConstant.DEFAULT_POPULAR_REVIEWS_COUNT;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService service;

    @GetMapping("/{id}")
    public ReviewDTO getReviewById(@PathVariable Long id) {
        log.info("Получаем один отзыв по id : {}", id);
        return service.findById(id);
    }

    @PostMapping
    @Validated({MarkerOfCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        log.info("Получен запрос Post, по отзыву: {}", reviewDTO);
        ReviewDTO review = service.saveReview(reviewDTO);
        log.info("Добавлен отзыв: {}", review);
        return review;
    }

    @PutMapping
    @Validated({MarkerOfUpdate.class})
    public ReviewDTO updateReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        log.info("Получен запрос PUT, на обновления данных по отзыву: {}", reviewDTO);
        ReviewDTO review = service.updateReview(reviewDTO);
        log.info("Добавлен или обновлен отзыв: {}", review);
        return review;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос DELETE, на удаления отзыва, по id: {}", id);
        service.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос PUT, на добавления лайка к отзыву, по id: {}", id);
        service.addRate(userId, id, true);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос DELETE, на удаление лайка к отзыву, по id: {}", id);
        service.deleteRate(userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос PUT, на добавления дизлайка к отзыву, по id: {}", id);
        service.addRate(userId, id, false);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос DELETE, на удаление дизлайка к отзыву, по id: {}", id);
        service.deleteRate(userId, id);
    }

    @GetMapping
    public List<ReviewDTO> getReviews(@Positive
                                      @RequestParam(defaultValue = DEFAULT_POPULAR_REVIEWS_COUNT)
                                      Integer count,
                                      @RequestParam(required = false)
                                      Long filmId
    ) {
        if (filmId != null) {
            log.info("Получен запрос GET, на получение топ {} отзывов по фильму {}.", count, filmId);
            List<ReviewDTO> reviewList = service.getPopularReviewByFilm(filmId, count);
            log.info("Получен топ {} отзывов по фильму: {}", count, reviewList.size());
            return reviewList;
        }
        log.info("Получен запрос GET, на получение топ {} отзывов по всем фильмам.", count);
        List<ReviewDTO> reviewList = service.getPopularReview(count);
        log.info("Получен топ {} отзывов по фильмам: {}", count, reviewList.size());
        return reviewList;
    }
}
