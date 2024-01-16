package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.yandex.practicum.filmorate.constants.FilmConstant.FILM_RELEASE;
import static ru.yandex.practicum.filmorate.constants.FilmConstant.POPULAR_FILMS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получен запрос Get.");
        return service.getAllFilm();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@Positive @RequestParam(defaultValue = POPULAR_FILMS) String count) {
        return service.getPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос Post.");
        validatedReleaseFilm(film);
        log.info("Добавлен фильм: {}", film.getName());
        return service.saveFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос Put.");
        validatedReleaseFilm(film);
        log.info("Обновлёна дата релиза: {}", film.getReleaseDate());
        return service.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId);
    }

    private void validatedReleaseFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILM_RELEASE)) {
            throw new ValidException("Data release before 28.12.1895 year", HttpStatus.BAD_REQUEST);
        }
    }
}
