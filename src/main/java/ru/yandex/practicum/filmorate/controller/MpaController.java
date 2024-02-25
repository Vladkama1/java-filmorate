package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDTO;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService service;

    @GetMapping("/{id}")
    public MpaDTO findById(@PathVariable Long id) {
        log.info("Получаем рейтинг фильма по id : {}", id);
        return service.findById(id);
    }

    @GetMapping
    public List<MpaDTO> getAllMPA() {
        log.info("Получен запрос GET, на получения всех рейтингов.");
        List<MpaDTO> mpaList = service.getAllMPA();
        log.info("Получен список всех рейтингов: {}", mpaList.size());
        return mpaList;
    }
}
