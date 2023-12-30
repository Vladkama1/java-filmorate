package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int id = 1;
    private LocalDate calendar = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен запрос Get.");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос Post.");
        if (film.getReleaseDate().isBefore(calendar)) {
            throw new ValidException("Data reliz before 28.12.1895 year", HttpStatus.NOT_FOUND);
        }
        film.setId(createId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}",film.getName());
        return film;
    }

    @PutMapping
    public Film put( @Valid @RequestBody Film film) {
        log.info("Получен запрос Put.");
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film this is not ID", HttpStatus.NOT_FOUND);
        }
        if (film.getReleaseDate().isBefore(calendar)) {
            throw new ValidException("Data reliz before 28.12.1895 year", HttpStatus.BAD_REQUEST);
        }
        films.put(film.getId(), film);
        log.info("Обновлёна дата релиза {}",film.getReleaseDate());
        return film;
    }

    private int createId() {
        return id++;
    }
}
