package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.FilmDTO;

import java.util.List;

public interface FilmService {
    FilmDTO findById(Long id);

    List<FilmDTO> getAllFilm();

    List<FilmDTO> getPopularFilms(String count);

    FilmDTO saveFilm(FilmDTO filmDTO);

    FilmDTO updateFilm(FilmDTO filmDTO);

    void deleteLike(Long id, Long userId);

    void delete(Long id);

    void addLike(Long filmId, Long userId);
}