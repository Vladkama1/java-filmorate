package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.anotation.MarkerOfCreate;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.yandex.practicum.filmorate.constants.FilmConstant.POPULAR_FILMS;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @GetMapping("/{id}")
    public FilmDTO getFilmById(@PathVariable Long id) {
        log.info("Запрос GET, на получение фильма по id {}.", id);
        FilmDTO film = service.findById(id);
        log.info("Получаем фильм по id: {}.", id);
        return film;
    }

    @GetMapping
    public List<FilmDTO> getAllFilms() {
        log.info("Запрос GET, на получения всех фильмов.");
        List<FilmDTO> filmList = service.getAllFilm();
        log.info("Получен список всех фильмов размером: {}.", filmList.size());
        return filmList;
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilms(@Positive @RequestParam(defaultValue = POPULAR_FILMS) Integer count,
                                         @RequestParam(required = false) Integer genreId,
                                         @RequestParam(required = false) Integer year) {
        log.info("Запрос GET, на получение топ {} фильмов по id: {} жанра за {} год.", count, genreId, year);
        List<FilmDTO> filmList = service.getPopularFilms(count, genreId, year);
        log.info("Получен топ {} фильмов: {}", count, filmList.size());
        return filmList;
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDTO> getFilmsByDirector(@PathVariable Long directorId, @RequestParam(defaultValue = "year") String sortBy) {
        log.info("Запрос GET, на получение фильмов режиссера с id {} отсортированных по {}.", directorId, sortBy);
        List<FilmDTO> filmList = service.getFilmsByDirectorId(directorId, sortBy);
        log.info("Получен список из {} фильмов, режиссера с id: {}.", filmList.size(), directorId);
        return filmList;
    }

    @PostMapping
    @Validated({MarkerOfCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO createFilm(@Valid @RequestBody FilmDTO filmDTO) {
        log.info("Запрос Post, по фильму: {}", filmDTO);
        FilmDTO film = service.saveFilm(filmDTO);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    @Validated({MarkerOfUpdate.class})
    public FilmDTO updateFilm(@Valid @RequestBody FilmDTO filmDTO) {
        log.info("Запрос Post, на обновления данных по фильму: {}", filmDTO);
        FilmDTO filmDTO1 = service.updateFilm(filmDTO);
        log.info("Добавлен или обновлен фильм: {}", filmDTO1);
        return filmDTO1;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос PUT, на добавления лайков, по id: {}", userId);
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Запрос DELETE, на удаления фильма, по id: {}", id);
        service.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос DELETE, на удаление лайков, по id: {}", id);
        service.deleteLike(id, userId);
    }
}
