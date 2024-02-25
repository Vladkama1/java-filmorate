package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.GenreDTO;

import java.util.List;

public interface GenreService {
    GenreDTO findById(Long id);

    List<GenreDTO> findAll();
}

