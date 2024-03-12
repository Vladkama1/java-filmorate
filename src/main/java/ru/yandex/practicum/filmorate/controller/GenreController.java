package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDTO;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService service;

    @GetMapping("/{id}")
    public GenreDTO findById(@PathVariable Long id) {
        log.info("Запрос GET, на получение жанра фильма по id : {}", id);
        return service.findById(id);
    }

    @GetMapping
    public List<GenreDTO> findAll() {
        log.info("Запрос GET, на получения всех жанров.");
        List<GenreDTO> genreList = service.findAll();
        log.info("Получен список всех жанров: {}", genreList.size());
        return genreList;
    }
}
