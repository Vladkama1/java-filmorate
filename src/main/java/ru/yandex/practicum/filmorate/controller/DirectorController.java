package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;
import ru.yandex.practicum.filmorate.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService service;

    @GetMapping("/{id}")
    public DirectorDTO getDirectorById(@PathVariable Long id) {
        log.info("Запрос GET, на получение режиссёра, по id: {}.", id);
        DirectorDTO director = service.findById(id);
        log.info("Получен режиссёр: {}.", director.getName());
        return director;
    }

    @GetMapping
    public List<DirectorDTO> getAllDirectors() {
        log.info("Запрос GET, на получения всех режиссёров.");
        List<DirectorDTO> directors = service.getAllDirector();
        log.info("Получен список всех режиссёров размером: {}.", directors.size());
        return directors;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDTO createDirector(@Valid @RequestBody DirectorDTO directorDTO) {
        log.info("Запрос Post, на оздание режиссёра: {}.", directorDTO.getName());
        DirectorDTO director = service.saveDirector(directorDTO);
        log.info("Создан режиссёр: {}.", directorDTO.getName());
        return director;
    }

    @PutMapping
    @Validated({MarkerOfUpdate.class})
    public DirectorDTO updateDirector(@Valid @RequestBody DirectorDTO directorDTO) {
        log.info("Запрос Put, на обновления данных режиссёра с id: {}", directorDTO.getId());
        DirectorDTO director = service.updateDirector(directorDTO);
        log.info("Обновлен режиссёр с id: {}", director.getId());
        return director;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Запрос DELETE, на удаления режиссёра, по id: {}", id);
        service.delete(id);
        log.info("Удален режиссёр с id: {}", id);
    }
}
