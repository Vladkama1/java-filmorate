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
        log.info("Получаем один фильм по id : {}", id);
        return service.findById(id);
    }

    @GetMapping
    public List<FilmDTO> getAllFilms() {
        log.info("Получен запрос GET, на получения всех фильмов.");
        List<FilmDTO> filmList = service.getAllFilm();
        log.info("Получен список всех фильмов: {}", filmList.size());
        return filmList;
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilms(@Positive @RequestParam(defaultValue = POPULAR_FILMS) String count) {
        log.info("Получен запрос GET, на получение топ {} фильмов.", count);
        List<FilmDTO> filmList = service.getPopularFilms(count);
        log.info("Получен топ {} фильмов: {}", count, filmList.size());
        return filmList;
    }

    @PostMapping
    @Validated({MarkerOfCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDTO createFilm(@Valid @RequestBody FilmDTO filmDTO) {
        log.info("Получен запрос Post, по фильму: {}", filmDTO);
        log.info("Добавлен фильм: {}", filmDTO);
        return service.saveFilm(filmDTO);
    }

    @PutMapping
    @Validated({MarkerOfUpdate.class})
    public FilmDTO updateFilm(@Valid @RequestBody FilmDTO filmDTO) {
        log.info("Получен запрос Post, на обновления данных по фильму: {}", filmDTO);
        FilmDTO filmDTO1 = service.updateFilm(filmDTO);
        log.info("Добавлен или обновлен фильм: {}", filmDTO1);
        return filmDTO1;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос PUT, на добавления лайков, по id: {}", userId);
        service.addLike(id, userId);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long filmId) {
        log.info("Получен запрос DELETE, на удаления фильма, по id: {}", filmId);
        service.delete(filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос DELETE, на удаление лайков, по id: {}", id);
        service.deleteLike(id, userId);
    }
}
